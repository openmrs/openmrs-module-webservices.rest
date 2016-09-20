/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.webservices.docs.swagger;

import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.atteo.evo.inflector.English;
import org.openmrs.api.context.Context;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.webservices.docs.ResourceRepresentation;
import org.openmrs.module.webservices.docs.SearchHandlerDoc;
import org.openmrs.module.webservices.docs.SearchQueryDoc;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription.Property;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubclassHandler;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.util.ReflectionUtils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class SwaggerSpecificationCreator {
	
	private SwaggerSpecification swaggerSpecification;
	
	private String baseUrl;
	
	private static List<SearchHandlerDoc> searchHandlerDocs;
	
	PrintStream originalErr;
	
	PrintStream originalOut;
	
	Map<Integer, Level> originalLevels = new HashMap<Integer, Level>();
	
	Map<String, Definition> definitionMap = new HashMap<String, Definition>();
	
	private Map<String, Tag> tags;
	
	private Logger log = Logger.getLogger(this.getClass());
	
	public SwaggerSpecificationCreator(String baseUrl) {
		this.swaggerSpecification = new SwaggerSpecification();
		this.baseUrl = baseUrl;
		List<SearchHandler> searchHandlers = Context.getService(RestService.class).getAllSearchHandlers();
		searchHandlerDocs = fillSearchHandlers(searchHandlers, baseUrl);
		tags = new HashMap<String, Tag>();
	}
	
	public String BuildJSON() {
		synchronized (this) {
			log.info("Initiating Swagger specification creation");
			toggleLogs(RestConstants.SWAGGER_LOGS_OFF);
			try {
				createApiDefinition();
				addPaths();
				addDefinitions();
				addSubclassOperations();
			}
			catch (Exception e) {
				log.error("Error while creating Swagger specification", e);
			}
			finally {
				toggleLogs(RestConstants.SWAGGER_LOGS_ON);
				log.info("Swagger specification creation complete");
			}
		}
		return createJSON();
	}
	
	private void addDefinitions() {
		Definitions definitions = new Definitions();
		definitions.setDefinitions(definitionMap);
		swaggerSpecification.setDefinitions(definitions);
	}
	
	private void toggleLogs(boolean targetState) {
		if (Context.getAdministrationService().getGlobalProperty(RestConstants.SWAGGER_QUIET_DOCS_GLOBAL_PROPERTY_NAME)
		        .equals("true")) {
			if (targetState == RestConstants.SWAGGER_LOGS_OFF) {
				// turn off the log4j loggers
				List<Logger> loggers = Collections.<Logger> list(LogManager.getCurrentLoggers());
				loggers.add(LogManager.getRootLogger());
				for (Logger logger : loggers) {
					originalLevels.put(logger.hashCode(), logger.getLevel());
					logger.setLevel(Level.OFF);
				}
				
				// silence stderr and stdout
				originalErr = System.err;
				System.setErr(new PrintStream(new OutputStream() {
					
					public void write(int b) {
						// noop
					}
				}));
				
				originalOut = System.out;
				System.setOut(new PrintStream(new OutputStream() {
					
					public void write(int b) {
						// noop
					}
				}));
			} else if (targetState == RestConstants.SWAGGER_LOGS_ON) {
				List<Logger> loggers = Collections.<Logger> list(LogManager.getCurrentLoggers());
				loggers.add(LogManager.getRootLogger());
				for (Logger logger : loggers) {
					logger.setLevel(originalLevels.get(logger.hashCode()));
				}
				
				System.setErr(originalErr);
				System.setOut(originalOut);
			}
		}
	}
	
	private void createApiDefinition() {
		Info info = new Info();
		// basic info
		info.setVersion(OpenmrsConstants.OPENMRS_VERSION_SHORT);
		info.setTitle("OpenMRS API Docs");
		info.setDescription("OpenMRS RESTful API specification");
		// contact
		info.setContact(new Contact("OpenMRS", "http://openmrs.org"));
		// license
		info.setLicense(new License("MPL-2.0 w/ HD", "http://openmrs.org/license"));
		// detailed versions
		info.setVersions(new Versions(OpenmrsConstants.OPENMRS_VERSION, getModuleVersions()));
		swaggerSpecification.setInfo(info);
		// security definitions
		swaggerSpecification.setSecurityDefinitions(new SecurityDefinitions("basic",
		        "HTTP basic access authentication using OpenMRS username and password"));
		List<String> produces = new ArrayList<String>();
		produces.add("application/json");
		produces.add("application/xml");
		List<String> consumes = new ArrayList<String>();
		consumes.add("application/json");
		// TODO: figure out how to post XML using Swagger UI
		//consumes.add("application/xml");
		swaggerSpecification.setHost(getBaseUrl());
		swaggerSpecification.setBasePath("/" + RestConstants.VERSION_1);
		swaggerSpecification.setProduces(produces);
		swaggerSpecification.setConsumes(consumes);
	}
	
	private List<ModuleVersion> getModuleVersions() {
		List<ModuleVersion> moduleVersions = new ArrayList<ModuleVersion>();
		
		for (Module module : ModuleFactory.getLoadedModules()) {
			moduleVersions.add(new ModuleVersion(module.getModuleId(), module.getVersion()));
		}
		
		return moduleVersions;
	}
	
	private boolean testOperationImplemented(OperationEnum operation, DelegatingResourceHandler<?> resourceHandler) {
		Method method;
		try {
			switch (operation) {
				case get:
					method = ReflectionUtils.findMethod(resourceHandler.getClass(), "getAll", RequestContext.class);
					
					if (method == null) {
						return false;
					} else {
						method.invoke(resourceHandler, new RequestContext());
					}
					
					break;
				case getSubresource:
					method = ReflectionUtils.findMethod(resourceHandler.getClass(), "getAll", String.class,
					    RequestContext.class);
					
					if (method == null) {
						return false;
					} else {
						method.invoke(resourceHandler, RestConstants.SWAGGER_IMPOSSIBLE_UNIQUE_ID, new RequestContext());
					}
					
					break;
				case getWithUUID:
				case getSubresourceWithUUID:
					method = ReflectionUtils.findMethod(resourceHandler.getClass(), "getByUniqueId", String.class);
					
					if (method == null) {
						return false;
					} else {
						method.invoke(resourceHandler, RestConstants.SWAGGER_IMPOSSIBLE_UNIQUE_ID);
					}
					
					break;
				case postCreate:
					method = ReflectionUtils.findMethod(resourceHandler.getClass(), "create", SimpleObject.class,
					    RequestContext.class);
					
					if (method == null) {
						return false;
					} else {
						try {
							// to avoid saving data to the database, we pass a null SimpleObject
							method.invoke(resourceHandler, null, new RequestContext());
						}
						catch (ResourceDoesNotSupportOperationException re) {
							return false;
						}
						catch (Exception ee) {
							// if the resource doesn't immediate throw ResourceDoesNotSupportOperationException
							// then we need to check if it's thrown in the save() method
							resourceHandler.save(null);
						}
					}
					
					break;
				case postSubresource:
					method = ReflectionUtils.findMethod(resourceHandler.getClass(), "create", String.class,
					    SimpleObject.class, RequestContext.class);
					
					if (method == null) {
						return false;
					} else {
						try {
							// to avoid saving data to the database, we pass a null SimpleObject
							method.invoke(resourceHandler, null, RestConstants.SWAGGER_IMPOSSIBLE_UNIQUE_ID,
							    new RequestContext());
						}
						catch (ResourceDoesNotSupportOperationException re) {
							return false;
						}
						catch (Exception ee) {
							// if the resource doesn't immediate throw ResourceDoesNotSupportOperationException
							// then we need to check if it's thrown in the save() method
							resourceHandler.save(null);
						}
					}
					
					break;
				case postUpdate:
					method = ReflectionUtils.findMethod(resourceHandler.getClass(), "update", String.class,
					    SimpleObject.class, RequestContext.class);
					
					if (method == null) {
						return false;
					} else {
						method.invoke(resourceHandler, RestConstants.SWAGGER_IMPOSSIBLE_UNIQUE_ID,
						    buildPOSTUpdateSimpleObject(resourceHandler), new RequestContext());
					}
					
					break;
				case postUpdateSubresouce:
					method = ReflectionUtils.findMethod(resourceHandler.getClass(), "update", String.class, String.class,
					    SimpleObject.class, RequestContext.class);
					
					if (method == null) {
						return false;
					} else {
						method.invoke(resourceHandler, RestConstants.SWAGGER_IMPOSSIBLE_UNIQUE_ID,
						    RestConstants.SWAGGER_IMPOSSIBLE_UNIQUE_ID, buildPOSTUpdateSimpleObject(resourceHandler),
						    new RequestContext());
					}
					
					break;
				case delete:
					method = ReflectionUtils.findMethod(resourceHandler.getClass(), "delete", String.class, String.class,
					    RequestContext.class);
					
					if (method == null) {
						return false;
					} else {
						method.invoke(resourceHandler, RestConstants.SWAGGER_IMPOSSIBLE_UNIQUE_ID, new String(),
						    new RequestContext());
					}
					
					break;
				case deleteSubresource:
					method = ReflectionUtils.findMethod(resourceHandler.getClass(), "delete", String.class, String.class,
					    String.class, RequestContext.class);
					
					if (method == null) {
						return false;
					} else {
						method.invoke(resourceHandler, RestConstants.SWAGGER_IMPOSSIBLE_UNIQUE_ID,
						    RestConstants.SWAGGER_IMPOSSIBLE_UNIQUE_ID, new String(), new RequestContext());
					}
					break;
				case purge:
					method = ReflectionUtils.findMethod(resourceHandler.getClass(), "purge", String.class,
					    RequestContext.class);
					
					if (method == null) {
						return false;
					} else {
						method.invoke(resourceHandler, RestConstants.SWAGGER_IMPOSSIBLE_UNIQUE_ID, new RequestContext());
					}
					
					break;
				case purgeSubresource:
					method = ReflectionUtils.findMethod(resourceHandler.getClass(), "purge", String.class, String.class,
					    RequestContext.class);
					
					if (method == null) {
						return false;
					} else {
						method.invoke(resourceHandler, RestConstants.SWAGGER_IMPOSSIBLE_UNIQUE_ID,
						    RestConstants.SWAGGER_IMPOSSIBLE_UNIQUE_ID, new RequestContext());
					}
			}
			return true;
		}
		catch (Exception e) {
			if (e instanceof ResourceDoesNotSupportOperationException
			        || e.getCause() instanceof ResourceDoesNotSupportOperationException) {
				return false;
			} else {
				return true;
			}
		}
	}
	
	private void sortResourceHandlers(List<DelegatingResourceHandler<?>> resourceHandlers) {
		Collections.sort(resourceHandlers, new Comparator<DelegatingResourceHandler<?>>() {
			
			@Override
			public int compare(DelegatingResourceHandler<?> left, DelegatingResourceHandler<?> right) {
				return isSubclass(left).compareTo(isSubclass(right));
			}
			
			private Boolean isSubclass(DelegatingResourceHandler<?> resourceHandler) {
				return resourceHandler.getClass().getAnnotation(SubResource.class) != null;
			}
		});
	}
	
	private void addResourceTag(String tagString) {
		if (!tags.containsKey(tagString)) {
			Tag tag = new Tag();
			tag.setName(tagString);
			tags.put(tagString, tag);
		}
	}
	
	private ResourceRepresentation getGETRepresentation(DelegatingResourceHandler<?> resourceHandler) {
		ResourceRepresentation getRepresentation = null;
		try {
			// first try the full representation
			getRepresentation = new ResourceRepresentation("GET", resourceHandler
			        .getRepresentationDescription(Representation.FULL).getProperties().keySet());
			return getRepresentation;
		}
		catch (Exception e) {
			// don't panic
		}
		try {
			// next try the default representation
			getRepresentation = new ResourceRepresentation("GET", resourceHandler
			        .getRepresentationDescription(Representation.DEFAULT).getProperties().keySet());
			return getRepresentation;
		}
		catch (Exception e) {
			// don't panic
		}
		return getRepresentation;
	}
	
	private ResourceRepresentation getPOSTCreateRepresentation(DelegatingResourceHandler<?> resourceHandler) {
		ResourceRepresentation postCreateRepresentation = null;
		try {
			DelegatingResourceDescription description = resourceHandler.getCreatableProperties();
			List<String> properties = getPOSTProperties(description);
			postCreateRepresentation = new ResourceRepresentation("POST create", properties);
		}
		catch (Exception e) {
			// don't panic
		}
		return postCreateRepresentation;
	}
	
	private SimpleObject buildPOSTUpdateSimpleObject(DelegatingResourceHandler<?> resourceHandler) {
		SimpleObject simpleObject = new SimpleObject();
		
		for (String property : resourceHandler.getUpdatableProperties().getProperties().keySet()) {
			simpleObject.put(property, property);
		}
		
		return simpleObject;
	}
	
	private ResourceRepresentation getPOSTUpdateRepresentation(DelegatingResourceHandler<?> resourceHandler) {
		ResourceRepresentation postCreateRepresentation = null;
		try {
			DelegatingResourceDescription description = resourceHandler.getUpdatableProperties();
			List<String> properties = getPOSTProperties(description);
			postCreateRepresentation = new ResourceRepresentation("POST update", properties);
		}
		catch (Exception e) {
			// don't panic
		}
		return postCreateRepresentation;
	}
	
	private Path buildFetchAllPath(Path path, DelegatingResourceHandler<?> resourceHandler, String resourceName,
	        String resourceParentName) {
		
		ResourceRepresentation getRepresentation = getGETRepresentation(resourceHandler);
		
		if (getRepresentation != null) {
			Operation getOperation = null;
			if (resourceParentName == null) {
				if (testOperationImplemented(OperationEnum.get, resourceHandler)) {
					
					getOperation = createOperation(resourceHandler, "get", resourceName, resourceParentName,
					    getRepresentation, OperationEnum.get);
				}
			} else {
				if (testOperationImplemented(OperationEnum.getSubresource, resourceHandler)) {
					getOperation = createOperation(resourceHandler, "get", resourceName, resourceParentName,
					    getRepresentation, OperationEnum.getSubresource);
				}
			}
			
			if (getOperation != null) {
				Map<String, Operation> operationsMap = path.getOperations();
				
				String tag = resourceParentName == null ? resourceName : resourceParentName;
				tag = tag.replaceAll("/", "_");
				addResourceTag(tag);
				
				getOperation.setTags(Arrays.asList(tag));
				operationsMap.put("get", getOperation);
				path.setOperations(operationsMap);
			}
		}
		
		return path;
	}
	
	private Path buildGetWithUUIDPath(Path path, DelegatingResourceHandler<?> resourceHandler, String resourceName,
	        String resourceParentName) {
		
		ResourceRepresentation getRepresentation = getGETRepresentation(resourceHandler);
		
		if (getRepresentation != null) {
			Operation getOperation = null;
			
			if (testOperationImplemented(OperationEnum.getWithUUID, resourceHandler)) {
				if (resourceParentName == null) {
					getOperation = createOperation(resourceHandler, "get", resourceName, resourceParentName,
					    getRepresentation, OperationEnum.getWithUUID);
				} else {
					getOperation = createOperation(resourceHandler, "get", resourceName, resourceParentName,
					    getRepresentation, OperationEnum.getSubresourceWithUUID);
				}
			}
			
			if (getOperation != null) {
				Map<String, Operation> operationsMap = path.getOperations();
				
				String tag = resourceParentName == null ? resourceName : resourceParentName;
				tag = tag.replaceAll("/", "_");
				addResourceTag(tag);
				
				getOperation.setTags(Arrays.asList(tag));
				operationsMap.put("get", getOperation);
				path.setOperations(operationsMap);
			}
		}
		
		return path;
	}
	
	private Path buildCreatePath(Path path, DelegatingResourceHandler<?> resourceHandler, String resourceName,
	        String resourceParentName) {
		
		ResourceRepresentation postCreateRepresentation = getPOSTCreateRepresentation(resourceHandler);
		
		if (postCreateRepresentation != null) {
			Operation postCreateOperation = null;
			
			if (resourceParentName == null) {
				if (testOperationImplemented(OperationEnum.postCreate, resourceHandler)) {
					postCreateOperation = createOperation(resourceHandler, "post", resourceName, resourceParentName,
					    postCreateRepresentation, OperationEnum.postCreate);
				}
			} else {
				if (testOperationImplemented(OperationEnum.postSubresource, resourceHandler)) {
					postCreateOperation = createOperation(resourceHandler, "post", resourceName, resourceParentName,
					    postCreateRepresentation, OperationEnum.postSubresource);
				}
			}
			
			if (postCreateOperation != null) {
				Map<String, Operation> operationsMap = path.getOperations();
				
				String tag = resourceParentName == null ? resourceName : resourceParentName;
				tag = tag.replaceAll("/", "_");
				addResourceTag(tag);
				
				postCreateOperation.setTags(Arrays.asList(tag));
				operationsMap.put("post", postCreateOperation);
				path.setOperations(operationsMap);
			}
		}
		
		return path;
	}
	
	private Path buildUpdatePath(Path path, DelegatingResourceHandler<?> resourceHandler, String resourceName,
	        String resourceParentName) {
		
		ResourceRepresentation postUpdateRepresentation = getPOSTUpdateRepresentation(resourceHandler);
		
		if (postUpdateRepresentation != null) {
			Operation postUpdateOperation = null;
			
			if (resourceParentName == null) {
				if (testOperationImplemented(OperationEnum.postUpdate, resourceHandler)) {
					postUpdateOperation = createOperation(resourceHandler, "post", resourceName, resourceParentName,
					    postUpdateRepresentation, OperationEnum.postUpdate);
				}
			} else {
				if (testOperationImplemented(OperationEnum.postUpdateSubresouce, resourceHandler)) {
					postUpdateOperation = createOperation(resourceHandler, "post", resourceName, resourceParentName,
					    postUpdateRepresentation, OperationEnum.postUpdateSubresouce);
				}
			}
			
			if (postUpdateOperation != null) {
				Map<String, Operation> operationsMap = path.getOperations();
				
				String tag = resourceParentName == null ? resourceName : resourceParentName;
				tag = tag.replaceAll("/", "_");
				addResourceTag(tag);
				
				postUpdateOperation.setTags(Arrays.asList(tag));
				operationsMap.put("post", postUpdateOperation);
				path.setOperations(operationsMap);
			}
		}
		
		return path;
	}
	
	private Path buildDeletePath(Path path, DelegatingResourceHandler<?> resourceHandler, String resourceName,
	        String resourceParentName) {
		
		Operation deleteOperation = null;
		
		if (resourceParentName == null) {
			if (testOperationImplemented(OperationEnum.delete, resourceHandler)) {
				deleteOperation = createOperation(resourceHandler, "delete", resourceName, resourceParentName,
				    new ResourceRepresentation("delete", new ArrayList()), OperationEnum.delete);
			}
		} else {
			if (testOperationImplemented(OperationEnum.deleteSubresource, resourceHandler)) {
				deleteOperation = createOperation(resourceHandler, "delete", resourceName, resourceParentName,
				    new ResourceRepresentation("delete", new ArrayList()), OperationEnum.deleteSubresource);
			}
		}
		
		if (deleteOperation != null) {
			Map<String, Operation> operationsMap = path.getOperations();
			
			String tag = resourceParentName == null ? resourceName : resourceParentName;
			tag = tag.replaceAll("/", "_");
			addResourceTag(tag);
			
			deleteOperation.setTags(Arrays.asList(tag));
			operationsMap.put("delete", deleteOperation);
			path.setOperations(operationsMap);
		}
		
		return path;
	}
	
	private Path buildPurgePath(Path path, DelegatingResourceHandler<?> resourceHandler, String resourceName,
	        String resourceParentName) {
		
		if (path.getOperations().containsKey("delete")) {
			// just add optional purge parameter
			Operation deleteOperation = path.getOperations().get("delete");
			
			deleteOperation.setSummary("Delete or purge resource by uuid");
			deleteOperation.setDescription("The resource will be voided/retired unless purge = 'true'");
			
			Parameter purge = new Parameter();
			purge.setName("purge");
			purge.setIn("query");
			purge.setType("boolean");
			
			List<Parameter> parameterList = deleteOperation.getParameters() == null ? new ArrayList<Parameter>()
			        : deleteOperation.getParameters();
			parameterList.add(purge);
			
			deleteOperation.setParameters(parameterList);
		} else {
			// create standalone purge operation with required
			Operation purgeOperation = null;
			
			if (resourceParentName == null) {
				if (testOperationImplemented(OperationEnum.purge, resourceHandler)) {
					purgeOperation = createOperation(resourceHandler, "delete", resourceName, resourceParentName,
					    new ResourceRepresentation("purge", new ArrayList()), OperationEnum.purge);
				}
			} else {
				if (testOperationImplemented(OperationEnum.purgeSubresource, resourceHandler)) {
					purgeOperation = createOperation(resourceHandler, "delete", resourceName, resourceParentName,
					    new ResourceRepresentation("purge", new ArrayList()), OperationEnum.purgeSubresource);
				}
			}
			
			if (purgeOperation != null) {
				Map<String, Operation> operationsMap = path.getOperations();
				
				String tag = resourceParentName == null ? resourceName : resourceParentName;
				tag = tag.replaceAll("/", "_");
				addResourceTag(tag);
				
				purgeOperation.setTags(Arrays.asList(tag));
				operationsMap.put("delete", purgeOperation);
				path.setOperations(operationsMap);
			}
		}
		
		return path;
	}
	
	private void addIndividualPath(Map<String, Path> pathMap, Path pathCheck, String resourceParentName,
	        String resourceName, Path path, String pathSuffix) {
		if (pathCheck != null) {
			if (resourceParentName == null) {
				pathMap.put("/" + resourceName + pathSuffix, path);
			} else {
				pathMap.put("/" + resourceParentName + "/{parent-uuid}/" + resourceName + pathSuffix, path);
			}
		}
	}
	
	private String buildSearchParameterDependencyString(Set<String> dependencies) {
		StringBuffer sb = new StringBuffer();
		
		sb.append("Must be used with ");
		sb.append(StringUtils.join(dependencies, ", "));
		
		String ret = sb.toString();
		int ind = ret.lastIndexOf(", ");
		
		if (ind > -1) {
			ret = new StringBuilder(ret).replace(ind, ind + 2, " or ").toString();
		}
		
		return ret;
	}
	
	private void addSearchOperations(DelegatingResourceHandler<?> resourceHandler, String resourceName,
	        String resourceParentName, Path getAllPath, Map<String, Path> pathMap) {
		boolean wasNew = false;
		
		if (resourceName != null && hasSearchHandler(resourceName)) {
			// if the path has no operations, add a note that search parameters are mandatory
			Operation get;
			if (getAllPath.getOperations().isEmpty() || getAllPath.getOperations().get("get") == null) {
				// create search-only operation
				get = new Operation();
				get.setName("get");
				
				get.setSummary("Search for " + resourceName);
				get.setDescription("At least one search parameter must be specified");
				
				// produces
				List<String> produces = new ArrayList<String>();
				produces.add("application/json");
				produces.add("application/xml");
				get.setProduces(produces);
				
				// schema
				Response statusOKResponse = new Response();
				statusOKResponse.setDescription(resourceName + " response");
				Schema schema = new Schema();
				
				// response
				statusOKResponse.setSchema(schema);
				List<String> resourceTags = new ArrayList<String>();
				resourceTags.add(resourceName);
				get.setTags(resourceTags);
				Map<String, Response> responses = new HashMap<String, Response>();
				responses.put("200", statusOKResponse);
				get.setResponses(responses);
				
				wasNew = true;
			} else {
				get = getAllPath.getOperations().get("get");
				get.setSummary("Fetch all non-retired " + resourceName + " resources or perform search");
				get.setDescription("All search parameters are optional");
			}
			
			Map<String, Parameter> parameterMap = new HashMap<String, Parameter>();
			
			// FIXME: this isn't perfect, it doesn't cover the case where multiple parameters are required together
			// FIXME: See https://github.com/OAI/OpenAPI-Specification/issues/256
			for (SearchHandler searchHandler : Context.getService(RestService.class).getAllSearchHandlers()) {
				
				String supportedResourceWithVersion = searchHandler.getSearchConfig().getSupportedResource();
				String supportedResource = supportedResourceWithVersion
				        .substring(supportedResourceWithVersion.indexOf('/') + 1);
				
				if (resourceName.equals(supportedResource)) {
					for (SearchQuery searchQuery : searchHandler.getSearchConfig().getSearchQueries()) {
						// parameters with no dependencies
						for (String requiredParameter : searchQuery.getRequiredParameters()) {
							Parameter p = new Parameter();
							p.setName(requiredParameter);
							p.setIn("query");
							parameterMap.put(requiredParameter, p);
						}
						// parameters with dependencies
						for (String requiredParameter : searchQuery.getOptionalParameters()) {
							Parameter p = new Parameter();
							p.setName(requiredParameter);
							p.setDescription(buildSearchParameterDependencyString(searchQuery.getRequiredParameters()));
							p.setIn("query");
							parameterMap.put(requiredParameter, p);
						}
					}
				}
			}
			
			// representations query parameter
			Parameter v = new Parameter();
			v.setName("v");
			v.setDescription("The representation to return (ref, default, full or custom)");
			v.setIn("query");
			v.setType("string");
			parameterMap.put("v", v);
			
			// query parameter
			Parameter q = new Parameter();
			q.setName("q");
			q.setDescription("The search query");
			q.setIn("query");
			q.setType("string");
			parameterMap.put("q", q);
			
			get.setParameters(new ArrayList(parameterMap.values()));
			get.getParameters().addAll(buildPagingParameters());
			get.setOperationId("getAll" + getOperationTitle(resourceHandler, true));
			
			if (wasNew) {
				getAllPath.getOperations().put("get", get);
				addIndividualPath(pathMap, getAllPath, resourceParentName, resourceName, getAllPath, "");
			}
		}
	}
	
	private void addPaths() {
		Map<String, Path> pathMap = new HashMap<String, Path>();
		
		// get all registered resource handlers
		List<DelegatingResourceHandler<?>> resourceHandlers = Context.getService(RestService.class).getResourceHandlers();
		sortResourceHandlers(resourceHandlers);
		
		// generate swagger JSON for each handler
		for (DelegatingResourceHandler<?> resourceHandler : resourceHandlers) {
			
			// get name and parent if it's a subresource
			Resource annotation = resourceHandler.getClass().getAnnotation(Resource.class);
			
			String resourceParentName = null;
			String resourceName = null;
			
			if (annotation != null) {
				// top level resource
				resourceName = annotation.name().substring(annotation.name().indexOf('/') + 1, annotation.name().length());
			} else {
				// subresource
				SubResource subResourceAnnotation = resourceHandler.getClass().getAnnotation(SubResource.class);
				
				if (subResourceAnnotation != null) {
					Resource parentResourceAnnotation = subResourceAnnotation.parent().getAnnotation(Resource.class);
					
					resourceName = subResourceAnnotation.path();
					resourceParentName = parentResourceAnnotation.name().substring(
					    parentResourceAnnotation.name().indexOf('/') + 1, parentResourceAnnotation.name().length());
				}
			}
			
			// subclass operations are handled separately in another method
			if (resourceHandler instanceof DelegatingSubclassHandler)
				continue;
			
			// set up paths
			Path rootPath = new Path();
			rootPath.setOperations(new HashMap<String, Operation>());
			
			Path uuidPath = new Path();
			uuidPath.setOperations(new HashMap<String, Operation>());
			
			/////////////////////////
			// GET all             //
			/////////////////////////
			Path rootPathGetAll = buildFetchAllPath(rootPath, resourceHandler, resourceName, resourceParentName);
			addIndividualPath(pathMap, rootPathGetAll, resourceParentName, resourceName, rootPathGetAll, "");
			
			/////////////////////////
			// GET search          //
			/////////////////////////
			addSearchOperations(resourceHandler, resourceName, resourceParentName, rootPathGetAll, pathMap);
			
			/////////////////////////
			// POST create         //
			/////////////////////////
			Path rootPathPostCreate = buildCreatePath(rootPathGetAll, resourceHandler, resourceName, resourceParentName);
			addIndividualPath(pathMap, rootPathPostCreate, resourceParentName, resourceName, rootPathPostCreate, "");
			
			/////////////////////////
			// GET with UUID       //
			/////////////////////////
			Path uuidPathGetAll = buildGetWithUUIDPath(uuidPath, resourceHandler, resourceName, resourceParentName);
			addIndividualPath(pathMap, uuidPathGetAll, resourceParentName, resourceName, uuidPathGetAll, "/{uuid}");
			
			/////////////////////////
			// POST update         //
			/////////////////////////
			Path uuidPathPostUpdate = buildUpdatePath(uuidPathGetAll, resourceHandler, resourceName, resourceParentName);
			addIndividualPath(pathMap, uuidPathGetAll, resourceParentName, resourceName, uuidPathPostUpdate, "/{uuid}");
			
			/////////////////////////
			// DELETE              //
			/////////////////////////
			Path uuidPathDelete = buildDeletePath(uuidPathPostUpdate, resourceHandler, resourceName, resourceParentName);
			//addIndividualPath(pathMap, uuidPathDelete, resourceParentName, resourceName, uuidPathDelete, "/{uuid}");
			
			/////////////////////////
			// DELETE (purge)      //
			/////////////////////////
			Path uuidPathPurge = buildPurgePath(uuidPathDelete, resourceHandler, resourceName, resourceParentName);
			addIndividualPath(pathMap, uuidPathPurge, resourceParentName, resourceName, uuidPathPurge, "/{uuid}");
		}
		
		Paths paths = new Paths();
		paths.setPaths(pathMap);
		swaggerSpecification.setPaths(paths);
		ArrayList<Tag> tagList = new ArrayList<Tag>(tags.values());
		Collections.sort(tagList);
		swaggerSpecification.setTags(tagList);
	}
	
	private void addSubclassOperations() {
		// FIXME: this needs to be improved a lot
		List<DelegatingResourceHandler<?>> resourceHandlers = Context.getService(RestService.class).getResourceHandlers();
		for (DelegatingResourceHandler<?> resourceHandler : resourceHandlers) {
			
			if (!(resourceHandler instanceof DelegatingSubclassHandler))
				continue;
			
			Class<?> resourceClass = ((DelegatingSubclassHandler<?, ?>) resourceHandler).getSuperclass();
			String resourceName = resourceClass.getSimpleName().toLowerCase();
			
			if (resourceName == null)
				continue;
			
			// 1. add non-optional enum property to model
			Path path = swaggerSpecification.getPaths().getPaths().get("/" + resourceName);
			if (path == null)
				continue;
			
			// FIXME: implement other operations when required
			Operation post = path.getOperations().get("post");
			if (post == null)
				continue;
			
			Definition definition = swaggerSpecification.getDefinitions().getDefinitions()
			        .get(StringUtils.capitalize(resourceName) + "Create");
			if (definition == null)
				continue;
			
			Properties properties = definition.getProperties();
			Map<String, DefinitionProperty> props = properties.getProperties();
			
			DefinitionProperty type = props.get("type");
			
			if (type == null) {
				type = new DefinitionProperty();
				properties.addProperty("type", type);
				type.setType("string");
				definition.addRequired("type");
			}
			
			type.addEnumerationItem(((DelegatingSubclassHandler) resourceHandler).getTypeName());
			
			// 2. merge subclass properties into definition
			for (String prop : resourceHandler.getRepresentationDescription(Representation.FULL).getProperties().keySet()) {
				if (props.get(prop) == null) {
					DefinitionProperty dp = new DefinitionProperty();
					dp.setType("string");
					props.put(prop, dp);
				}
			}
			
			// 3. update description
			post.setDescription("Certain properties may be required depending on type");
		}
	}
	
	private static List<String> getPOSTProperties(DelegatingResourceDescription description) {
		List<String> properties = new ArrayList<String>();
		for (Entry<String, Property> property : description.getProperties().entrySet()) {
			if (property.getValue().isRequired()) {
				properties.add("*" + property.getKey() + "*");
			} else {
				properties.add(property.getKey());
			}
		}
		return properties;
	}
	
	private List<Parameter> getParametersListForSearchHandlers(String resourceName, String searchHandlerId, int queryIndex) {
		List<Parameter> parameters = new ArrayList<Parameter>();
		String resourceURL = getResourceUrl(getBaseUrl(), resourceName);
		for (SearchHandlerDoc searchDoc : searchHandlerDocs) {
			if (searchDoc.getSearchHandlerId().equals(searchHandlerId) && searchDoc.getResourceURL().equals(resourceURL)) {
				SearchQueryDoc queryDoc = searchDoc.getSearchQueriesDoc().get(queryIndex);
				for (String requiredParameter : queryDoc.getRequiredParameters()) {
					Parameter parameter = new Parameter();
					parameter.setName(requiredParameter);
					parameter.setIn("query");
					parameter.setDescription("");
					parameter.setRequired(true);
					parameters.add(parameter);
				}
				for (String optionalParameter : queryDoc.getOptionalParameters()) {
					Parameter parameter = new Parameter();
					parameter.setName(optionalParameter);
					parameter.setIn("query");
					parameter.setDescription("");
					parameter.setRequired(false);
					parameters.add(parameter);
				}
				
				break;
			}
		}
		return parameters;
		
	}
	
	private String createJSON() {
		String json = "";
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
			mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, true);
			mapper.setSerializationInclusion(Include.NON_NULL);
			mapper.getSerializerProvider().setNullKeySerializer(new NullSerializer());
			
			json = mapper.writeValueAsString(swaggerSpecification);
		}
		catch (Exception exp) {
			exp.printStackTrace();
		}
		
		return json;
	}
	
	private Parameter buildRequiredUUIDParameter(String name, String label) {
		Parameter parameter = new Parameter();
		
		parameter.setName(name);
		parameter.setIn("path");
		parameter.setDescription(label);
		parameter.setRequired(true);
		
		return parameter;
	}
	
	private List<Parameter> buildPagingParameters() {
		List<Parameter> pagingParams = new ArrayList<Parameter>();
		
		Parameter limit = new Parameter();
		limit.setName("limit");
		limit.setDescription("The number of results to return");
		limit.setIn("query");
		limit.setType("integer");
		pagingParams.add(limit);
		
		Parameter startIndex = new Parameter();
		startIndex.setName("startIndex");
		startIndex.setDescription("The offset at which to start");
		startIndex.setIn("query");
		startIndex.setType("integer");
		pagingParams.add(startIndex);
		
		return pagingParams;
	}
	
	private Parameter buildPOSTBodyParameter(String resourceName, String resourceParentName, OperationEnum operationEnum) {
		Parameter parameter = new Parameter();
		Schema bodySchema = new Schema();
		
		parameter.setIn("body");
		parameter.setRequired(true);
		parameter.setSchema(bodySchema);
		
		switch (operationEnum) {
			case postCreate:
			case postSubresource:
				parameter.setName("resource");
				parameter.setDescription("Resource to create");
				break;
			case postUpdate:
			case postUpdateSubresouce:
				parameter.setName("resource");
				parameter.setDescription("Resource properties to update");
		}
		
		bodySchema.setRef(getSchemaRef(resourceName, resourceParentName, operationEnum));
		
		return parameter;
	}
	
	private String getSchemaName(String resourceName, String resourceParentName, OperationEnum operationEnum) {
		
		String suffix = "";
		
		switch (operationEnum) {
			case get:
			case getSubresource:
			case getWithUUID:
			case getSubresourceWithUUID:
				suffix = "Get";
				break;
			case postCreate:
			case postSubresource:
				suffix = "Create";
				break;
			case postUpdate:
			case postUpdateSubresouce:
				suffix = "Update";
				break;
		}
		
		String modelRefName;
		
		if (resourceParentName == null) {
			modelRefName = StringUtils.capitalize(resourceName) + suffix;
		} else {
			modelRefName = StringUtils.capitalize(resourceParentName) + StringUtils.capitalize(resourceName) + suffix;
		}
		
		// get rid of slashes in model names
		String[] split = modelRefName.split("\\/");
		String ret = "";
		for (String s : split) {
			ret += StringUtils.capitalize(s);
		}
		
		return ret;
	}
	
	private String getSchemaRef(String resourceName, String resourceParentName, OperationEnum operationEnum) {
		return "#/definitions/" + getSchemaName(resourceName, resourceParentName, operationEnum);
	}
	
	private String getModelTitle(String schemaName) {
		if (schemaName.toLowerCase().endsWith("get")) {
			return schemaName.substring(0, schemaName.length() - 3);
		} else if (schemaName.toLowerCase().endsWith("create") || schemaName.toLowerCase().endsWith("update")) {
			return schemaName.substring(0, schemaName.length() - 6);
		}
		return schemaName;
	}
	
	private String getOperationTitle(DelegatingResourceHandler<?> resourceHandler, Boolean pluralize) {
		StringBuilder ret = new StringBuilder();
		English inflector = new English();
		
		// get rid of slashes
		String simpleClassName = resourceHandler.getClass().getSimpleName();
		
		// get rid of 'Resource' and version number suffixes
		simpleClassName = simpleClassName.replaceAll("\\d_\\d{1,2}$", "");
		simpleClassName = simpleClassName.replaceAll("Resource$", "");
		
		// pluralize if require
		if (pluralize) {
			String[] words = simpleClassName.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
			String suffix = words[words.length - 1];
			
			for (int i = 0; i < words.length - 1; i++) {
				ret.append(words[i]);
			}
			
			ret.append(inflector.getPlural(suffix));
		} else {
			ret.append(simpleClassName);
		}
		
		return ret.toString();
	}
	
	private void createDefinition(OperationEnum operationEnum, String resourceName, String resourceParentName,
	        ResourceRepresentation representation) {
		
		String definitionName = getSchemaName(resourceName, resourceParentName, operationEnum);
		
		Definition definition = new Definition();
		definition.setType("object");
		
		Xml xml = new Xml();
		xml.setName(getModelTitle(getSchemaName(resourceName, resourceParentName, operationEnum).toLowerCase()));
		definition.setXml(xml);
		
		Properties props = new Properties();
		definition.setProperties(props);
		
		Collection<String> properties = representation.getProperties();
		
		for (String property : properties) {
			DefinitionProperty defProp = new DefinitionProperty();
			String propName;
			
			if (property.startsWith("*")) {
				propName = property.replace("*", "");
				definition.addRequired(propName);
			} else {
				propName = property;
			}
			
			defProp.setType("string");
			props.addProperty(propName, defProp);
		}
		
		definitionMap.put(definitionName, definition);
	}
	
	private Operation createOperation(DelegatingResourceHandler<?> resourceHandler, String operationName,
	        String resourceName, String resourceParentName, ResourceRepresentation representation,
	        OperationEnum operationEnum) {
		Map<String, Response> responses = new HashMap<String, Response>();
		
		Operation operation = new Operation();
		operation.setName(operationName);
		operation.setDescription(null);
		
		List<String> produces = new ArrayList<String>();
		produces.add("application/json");
		produces.add("application/xml");
		operation.setProduces(produces);
		List<Parameter> parameters = new ArrayList<Parameter>();
		
		operation.setParameters(parameters);
		
		// create definition
		if (operationName == "post" || operationName == "get") {
			createDefinition(operationEnum, resourceName, resourceParentName, representation);
		}
		
		// 200 response (Successful operation)
		Response statusOKResponse = new Response();
		statusOKResponse.setDescription(resourceName + " response");
		Schema responseBodySchema = new Schema();
		
		// 201 response (Successfully created)
		Response createdOKResponse = new Response();
		createdOKResponse.setDescription(resourceName + " response");
		createdOKResponse.setSchema(responseBodySchema);
		
		// 204 delete success
		Response deletedOKResponse = new Response();
		deletedOKResponse.setDescription("Delete successful");
		
		// 401 response (User not logged in)
		Response notLoggedInResponse = new Response();
		notLoggedInResponse.setDescription("User not logged in");
		
		// 404 (Object with given uuid doesn't exist)
		Response notFoundResponse = new Response();
		notFoundResponse.setDescription("Resource with given uuid doesn't exist");
		
		// representations query parameter
		Parameter v = new Parameter();
		v.setName("v");
		v.setDescription("The representation to return (ref, default, full or custom)");
		v.setIn("query");
		v.setType("string");
		
		// query parameter
		Parameter q = new Parameter();
		q.setName("q");
		q.setDescription("The search query");
		q.setIn("query");
		q.setType("string");
		
		if (operationEnum == OperationEnum.get) {
			
			operation.setSummary("Fetch all non-retired");
			operation.setOperationId("getAll" + getOperationTitle(resourceHandler, true));
			responseBodySchema.setRef(getSchemaRef(resourceName, resourceParentName, OperationEnum.get));
			parameters.add(v);
			parameters.add(q);
			parameters.addAll(buildPagingParameters());
			statusOKResponse.setSchema(responseBodySchema);
			responses.put("200", statusOKResponse);
			
		} else if (operationEnum == OperationEnum.getWithUUID) {
			
			operation.setSummary("Fetch by uuid");
			operation.setOperationId("get" + getOperationTitle(resourceHandler, false));
			responseBodySchema.setRef(getSchemaRef(resourceName, resourceParentName, OperationEnum.getWithUUID));
			parameters.add(buildRequiredUUIDParameter("uuid", "uuid to filter by"));
			parameters.add(v);
			statusOKResponse.setSchema(responseBodySchema);
			responses.put("200", statusOKResponse);
			responses.put("404", notFoundResponse);
			
		} else if (operationEnum == OperationEnum.postCreate) {
			
			operation.setSummary("Create with properties in request");
			operation.setOperationId("create" + getOperationTitle(resourceHandler, false));
			responseBodySchema.setRef(getSchemaRef(resourceName, resourceParentName, OperationEnum.get));
			parameters.add(buildPOSTBodyParameter(resourceName, resourceParentName, OperationEnum.postCreate));
			responses.put("201", createdOKResponse);
			
		} else if (operationEnum == OperationEnum.postUpdate) {
			
			operation.setSummary("Edit with given uuid, only modifying properties in request");
			operation.setOperationId("update" + getOperationTitle(resourceHandler, false));
			responseBodySchema.setRef(getSchemaRef(resourceName, resourceParentName, OperationEnum.get));
			parameters.add(buildRequiredUUIDParameter("uuid", "uuid of resource to update"));
			parameters.add(buildPOSTBodyParameter(resourceName, resourceParentName, OperationEnum.postUpdate));
			responses.put("201", createdOKResponse);
			
		} else if (operationEnum == OperationEnum.getSubresource) {
			
			operation.setSummary("Fetch all non-retired " + resourceName + " subresources");
			operation.setOperationId("getAll" + getOperationTitle(resourceHandler, true));
			parameters.add(buildRequiredUUIDParameter("parent-uuid", "parent resource uuid"));
			responseBodySchema.setRef(getSchemaRef(resourceName, resourceParentName, OperationEnum.get));
			parameters.add(v);
			parameters.add(q);
			parameters.addAll(buildPagingParameters());
			statusOKResponse.setSchema(responseBodySchema);
			responses.put("200", statusOKResponse);
			
		} else if (operationEnum == OperationEnum.postSubresource) {
			
			operation.setSummary("Create " + resourceName + " subresource with properties in request");
			operation.setOperationId("create" + getOperationTitle(resourceHandler, false));
			parameters.add(buildRequiredUUIDParameter("parent-uuid", "parent resource uuid"));
			responseBodySchema.setRef(getSchemaRef(resourceName, resourceParentName, OperationEnum.get));
			parameters.add(buildPOSTBodyParameter(resourceName, resourceParentName, OperationEnum.postSubresource));
			responses.put("201", createdOKResponse);
			
		} else if (operationEnum == OperationEnum.postUpdateSubresouce) {
			
			operation.setSummary("Edit " + resourceName
			        + " subresource with given uuid, only modifying properties in request");
			operation.setOperationId("update" + getOperationTitle(resourceHandler, false));
			parameters.add(buildRequiredUUIDParameter("parent-uuid", "parent resource uuid"));
			parameters.add(buildRequiredUUIDParameter("uuid", "uuid of resource to update"));
			responseBodySchema.setRef(getSchemaRef(resourceName, resourceParentName, OperationEnum.get));
			parameters.add(buildPOSTBodyParameter(resourceName, resourceParentName, OperationEnum.postUpdateSubresouce));
			responses.put("201", createdOKResponse);
			
		} else if (operationEnum == OperationEnum.getSubresourceWithUUID) {
			
			operation.setSummary("Fetch " + resourceName + " subresources by uuid");
			operation.setOperationId("get" + getOperationTitle(resourceHandler, false));
			responseBodySchema.setRef(getSchemaRef(resourceName, resourceParentName, OperationEnum.getSubresourceWithUUID));
			parameters.add(buildRequiredUUIDParameter("parent-uuid", "parent resource uuid"));
			parameters.add(buildRequiredUUIDParameter("uuid", "uuid to filter by"));
			parameters.add(v);
			statusOKResponse.setSchema(responseBodySchema);
			responses.put("200", statusOKResponse);
			responses.put("404", notFoundResponse);
			
		} else if (operationEnum == OperationEnum.delete) {
			
			operation.setSummary("Delete resource by uuid");
			operation.setOperationId("delete" + getOperationTitle(resourceHandler, false));
			statusOKResponse.setDescription("Successful operation");
			parameters.add(buildRequiredUUIDParameter("uuid", "uuid to delete"));
			responses.put("204", deletedOKResponse);
			responses.put("404", notFoundResponse);
			
		} else if (operationEnum == OperationEnum.deleteSubresource) {
			
			operation.setSummary("Delete " + resourceName + " subresource by uuid");
			operation.setOperationId("delete" + getOperationTitle(resourceHandler, false));
			statusOKResponse.setDescription("Successful operation");
			parameters.add(buildRequiredUUIDParameter("parent-uuid", "parent resource uuid"));
			parameters.add(buildRequiredUUIDParameter("uuid", "uuid to delete"));
			responses.put("204", deletedOKResponse);
			responses.put("404", notFoundResponse);
			
		} else if (operationEnum == OperationEnum.purge) {
			
			operation.setSummary("Purge resource by uuid");
			operation.setOperationId("purge" + getOperationTitle(resourceHandler, false));
			statusOKResponse.setDescription("Successful operation");
			parameters.add(buildRequiredUUIDParameter("uuid", "uuid to delete"));
			responses.put("204", deletedOKResponse);
			
		} else if (operationEnum == OperationEnum.purgeSubresource) {
			
			operation.setSummary("Purge " + resourceName + " subresource by uuid");
			operation.setOperationId("purge" + getOperationTitle(resourceHandler, false));
			statusOKResponse.setDescription("Successful operation");
			parameters.add(buildRequiredUUIDParameter("parent-uuid", "parent resource uuid"));
			parameters.add(buildRequiredUUIDParameter("uuid", "uuid to delete"));
			responses.put("204", deletedOKResponse);
		}
		
		List<String> resourceTags = new ArrayList<String>();
		resourceTags.add(resourceName);
		operation.setTags(resourceTags);
		responses.put("401", notLoggedInResponse);
		operation.setResponses(responses);
		
		return operation;
	}
	
	private Operation createSearchHandlerOperation(String operationName, String resourceName, String searchHandlerId,
	        OperationEnum operationEnum, int queryIndex) {
		
		Operation operation = new Operation();
		operation.setName(operationName);
		operation.setDescription(null);
		List<String> produces = new ArrayList<String>();
		produces.add("application/json");
		operation.setProduces(produces);
		operation.setIsSearchHandler("true");
		List<Parameter> parameters = new ArrayList<Parameter>();
		
		parameters = getParametersListForSearchHandlers(resourceName, searchHandlerId, queryIndex);
		
		operation.setParameters(parameters);
		
		Response statusOKResponse = new Response();
		statusOKResponse.setDescription(resourceName + " response");
		Schema schema = new Schema();
		
		schema.setRef("#/definitions/" + resourceName);
		
		statusOKResponse.setSchema(schema);
		
		List<String> resourceTags = new ArrayList<String>();
		resourceTags.add(resourceName);
		operation.setTags(resourceTags);
		
		Map<String, Response> responses = new HashMap<String, Response>();
		responses.put("200", statusOKResponse);
		
		operation.setResponses(responses);
		
		String resourceURL = getResourceUrl(getBaseUrl(), resourceName);
		for (SearchHandlerDoc searchDoc : searchHandlerDocs) {
			if (searchDoc.getSearchHandlerId().equals(searchHandlerId) && searchDoc.getResourceURL().equals(resourceURL)) {
				SearchQueryDoc queryDoc = searchDoc.getSearchQueriesDoc().get(queryIndex);
				operation.setSummary(queryDoc.getDescription());
			}
		}
		
		return operation;
	}
	
	private static List<SearchHandlerDoc> fillSearchHandlers(List<SearchHandler> searchHandlers, String url) {
		
		List<SearchHandlerDoc> searchHandlerDocList = new ArrayList<SearchHandlerDoc>();
		String baseUrl = url.replace("/rest", "");
		
		for (int i = 0; i < searchHandlers.size(); i++) {
			if (searchHandlers.get(i) != null) {
				SearchHandler searchHandler = searchHandlers.get(i);
				SearchHandlerDoc searchHandlerDoc = new SearchHandlerDoc(searchHandler, baseUrl);
				searchHandlerDocList.add(searchHandlerDoc);
			}
		}
		
		return searchHandlerDocList;
	}
	
	private String getResourceUrl(String baseUrl, String resourceName) {
		
		String resourceUrl = baseUrl;
		
		//Set the root url.
		return resourceUrl + "/v1/" + resourceName;
	}
	
	private boolean hasSearchHandler(String resourceName) {
		for (SearchHandlerDoc doc : searchHandlerDocs) {
			if (doc.getResourceURL().contains(resourceName)) {
				return true;
			}
		}
		
		return false;
	}
	
	public String getBaseUrl() {
		return baseUrl;
	}
	
	public SwaggerSpecification getSwaggerSpecification() {
		return swaggerSpecification;
	}
}
