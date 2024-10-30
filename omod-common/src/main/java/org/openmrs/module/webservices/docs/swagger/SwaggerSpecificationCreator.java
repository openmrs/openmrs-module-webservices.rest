/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.docs.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.parameters.QueryParameter;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.Paths;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.atteo.evo.inflector.English;
import org.openmrs.api.context.Context;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.webservices.docs.SearchHandlerDoc;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchParameter;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubclassHandler;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.util.ReflectionUtils;

import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SwaggerSpecificationCreator {

	private static OpenAPI openAPI;

	private static String cachedJson;

	private String host;

	private String basePath;

	private List<Scheme> schemes;

	private String baseUrl;

	private static List<SearchHandlerDoc> searchHandlerDocs;

	PrintStream originalErr;

	PrintStream originalOut;

	private Parameter subclassTypeParameter = new Parameter()
			.name("t")
			.in("query")
			.description("The type of Subclass Resource to return")
			.schema(new StringSchema());

	Map<Integer, Level> originalLevels = new HashMap<Integer, Level>();

	private Logger log = Logger.getLogger(this.getClass());

	public SwaggerSpecificationCreator() {
	}

	public SwaggerSpecificationCreator host(String host) {
		this.host = host;
		return this;
	}

	public SwaggerSpecificationCreator basePath(String basePath) {
		this.basePath = basePath;
		return this;
	}

	public SwaggerSpecificationCreator scheme(Scheme scheme) {
		if (schemes == null) {
			this.schemes = new ArrayList<Scheme>();
		}
		if (!schemes.contains(scheme)) {
			this.schemes.add(scheme);
		}
		return this;
	}

	@io.swagger.v3.oas.annotations.media.Schema (description = "Scheme type for API communication")
	public enum Scheme {
		@io.swagger.v3.oas.annotations.media.Schema (description = "Hypertext Transfer Protocol")
		HTTP,

		@io.swagger.v3.oas.annotations.media.Schema (description = "Hypertext Transfer Protocol Secure")
		HTTPS,

		@io.swagger.v3.oas.annotations.media.Schema (description = "WebSocket Protocol")
		WS,

		@io.swagger.v3.oas.annotations.media.Schema(description = "WebSocket Secure Protocol")
		WSS
	}

	/**
	 * Regenerate the swagger spec from scratch
	 */
	private void BuildJSON() {
		log.info("Initiating Swagger specification creation");
		toggleLogs(SwaggerConstants.SWAGGER_LOGS_OFF);
		try {
			initOpenAPI();
			addPaths();
			addDefaultDefinitions();
			//				addSubclassOperations(); //FIXME uncomment after fixing the method
		}
		catch (Exception e) {
			log.error("Error while creating Swagger specification", e);
		}
		finally {
			toggleLogs(SwaggerConstants.SWAGGER_LOGS_ON);
		}
	}

	public String getJSON() {
		if (isCached() && cachedJson != null) {
			log.info("Returning a cached copy of Swagger specification");
			return cachedJson;
		}

		openAPI = new OpenAPI();
		BuildJSON();
		cachedJson = createJSON();  // Cache the JSON string
		return cachedJson;
	}

	private void addDefaultDefinitions() {
		// schema of the default response
		// received from fetchAll and search operations
		Components components = openAPI.getComponents();
		components.addSchemas("FetchAll", new ObjectSchema()
				.addProperty("results", new ArraySchema()
						.items(new ObjectSchema()
								.addProperty("uuid", new StringSchema())
								.addProperty("display", new StringSchema())
								.addProperty("links", new ArraySchema()
										.items(new ObjectSchema()
												.addProperty("rel", new StringSchema().example("self"))
												.addProperty("uri", new StringSchema().format("uri")))))));

		openAPI.setComponents(components);
	}

	@SuppressWarnings("unchecked")
	private void toggleLogs(boolean targetState) {
		if (Context.getAdministrationService().getGlobalProperty(SwaggerConstants.SWAGGER_QUIET_DOCS_GLOBAL_PROPERTY_NAME)
				.equals("true")) {
			if (targetState == SwaggerConstants.SWAGGER_LOGS_OFF) {
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
			} else if (targetState == SwaggerConstants.SWAGGER_LOGS_ON) {
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

	private void initOpenAPI() {
		final Info info = new Info()
				.version(OpenmrsConstants.OPENMRS_VERSION_SHORT)
				.title("OpenMRS API Docs")
				.description("OpenMRS RESTful API documentation generated by Swagger")
				.contact(new Contact().name("OpenMRS").url("http://openmrs.org"))
				.license(new License().name("MPL-2.0 w/ HD").url("http://openmrs.org/license"));

		openAPI = new OpenAPI()
				.openapi("3.0.0")
				.info(info)
				.addServersItem(new Server().url(this.host + this.basePath))
				.components(new Components().addSecuritySchemes("basic_auth", new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic")))
				.addSecurityItem(new SecurityRequirement().addList("basic_auth"))
				.externalDocs(new ExternalDocumentation()
						.description("Find more info on REST Module Wiki")
						.url("https://wiki.openmrs.org/x/xoAaAQ"));

		openAPI.setPaths(new Paths());
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
						method.invoke(resourceHandler, SwaggerConstants.SWAGGER_IMPOSSIBLE_UNIQUE_ID, new RequestContext());
					}

					break;
				case getSubresource:
					method = ReflectionUtils.findMethod(resourceHandler.getClass(), "getAll", String.class,
							RequestContext.class);

					if (method == null) {
						return false;
					} else {
						method.invoke(resourceHandler, SwaggerConstants.SWAGGER_IMPOSSIBLE_UNIQUE_ID, new RequestContext());
					}

					break;
				case getWithUUID:
				case getSubresourceWithUUID:
					method = ReflectionUtils.findMethod(resourceHandler.getClass(), "getByUniqueId", String.class);

					if (method == null) {
						return false;
					} else {
						method.invoke(resourceHandler, SwaggerConstants.SWAGGER_IMPOSSIBLE_UNIQUE_ID);
					}

					break;
				case getWithDoSearch:
					method = ReflectionUtils.findMethod(resourceHandler.getClass(), "search", RequestContext.class);

					if (method == null) {
						return false;
					} else {
						method.invoke(resourceHandler, new RequestContext());
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
							method.invoke(resourceHandler, null, SwaggerConstants.SWAGGER_IMPOSSIBLE_UNIQUE_ID,
									new RequestContext());
						}
						catch (InvocationTargetException e) {
							if (e.getCause() instanceof ResourceDoesNotSupportOperationException) {
								return false;
							}
							resourceHandler.save(null);
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
						method.invoke(resourceHandler, SwaggerConstants.SWAGGER_IMPOSSIBLE_UNIQUE_ID,
								buildPOSTUpdateSimpleObject(resourceHandler), new RequestContext());
					}

					break;
				case postUpdateSubresouce:
					method = ReflectionUtils.findMethod(resourceHandler.getClass(), "update", String.class, String.class,
							SimpleObject.class, RequestContext.class);

					if (method == null) {
						return false;
					} else {
						method.invoke(resourceHandler, SwaggerConstants.SWAGGER_IMPOSSIBLE_UNIQUE_ID,
								SwaggerConstants.SWAGGER_IMPOSSIBLE_UNIQUE_ID, buildPOSTUpdateSimpleObject(resourceHandler),
								new RequestContext());
					}

					break;
				case delete:
					method = ReflectionUtils.findMethod(resourceHandler.getClass(), "delete", String.class, String.class,
							RequestContext.class);

					if (method == null) {
						return false;
					} else {
						method.invoke(resourceHandler, SwaggerConstants.SWAGGER_IMPOSSIBLE_UNIQUE_ID, "",
								new RequestContext());
					}

					break;
				case deleteSubresource:
					method = ReflectionUtils.findMethod(resourceHandler.getClass(), "delete", String.class, String.class,
							String.class, RequestContext.class);

					if (method == null) {
						return false;
					} else {
						method.invoke(resourceHandler, SwaggerConstants.SWAGGER_IMPOSSIBLE_UNIQUE_ID,
								SwaggerConstants.SWAGGER_IMPOSSIBLE_UNIQUE_ID, "", new RequestContext());
					}
					break;
				case purge:
					method = ReflectionUtils.findMethod(resourceHandler.getClass(), "purge", String.class,
							RequestContext.class);

					if (method == null) {
						return false;
					} else {
						method.invoke(resourceHandler, SwaggerConstants.SWAGGER_IMPOSSIBLE_UNIQUE_ID, new RequestContext());
					}

					break;
				case purgeSubresource:
					method = ReflectionUtils.findMethod(resourceHandler.getClass(), "purge", String.class, String.class,
							RequestContext.class);

					if (method == null) {
						return false;
					} else {
						method.invoke(resourceHandler, SwaggerConstants.SWAGGER_IMPOSSIBLE_UNIQUE_ID,
								SwaggerConstants.SWAGGER_IMPOSSIBLE_UNIQUE_ID, new RequestContext());
					}
			}
			return true;
		}
		catch (Exception e) {
			return !(e instanceof ResourceDoesNotSupportOperationException)
					&& !(e.getCause() instanceof ResourceDoesNotSupportOperationException);
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

	private SimpleObject buildPOSTUpdateSimpleObject(DelegatingResourceHandler<?> resourceHandler) {
		SimpleObject simpleObject = new SimpleObject();

		for (String property : resourceHandler.getUpdatableProperties().getProperties().keySet()) {
			simpleObject.put(property, property);
		}

		return simpleObject;
	}

	private PathItem buildFetchAllPath(PathItem path,
								   DelegatingResourceHandler<?> resourceHandler, String resourceName, String resourceParentName) {
		io.swagger.v3.oas.models.Operation getOperation = null;
		if (resourceParentName == null) {
			if (testOperationImplemented(OperationEnum.get, resourceHandler)) {
				getOperation = createOperation(resourceHandler, "get", resourceName, null,
						OperationEnum.get);
			}
		} else {
			if (testOperationImplemented(OperationEnum.getSubresource, resourceHandler)) {
				getOperation = createOperation(resourceHandler, "get", resourceName, resourceParentName,
						OperationEnum.getSubresource);
			}
		}

		if (getOperation != null) {
			path.setGet(getOperation);
		}

		return path;
	}

	private PathItem buildGetWithUUIDPath(PathItem path,
									  DelegatingResourceHandler<?> resourceHandler, String resourceName, String resourceParentName) {

		io.swagger.v3.oas.models.Operation getOperation = null;

		if (testOperationImplemented(OperationEnum.getWithUUID, resourceHandler)) {
			if (resourceParentName == null) {
				getOperation = createOperation(resourceHandler, "get", resourceName, null,
						OperationEnum.getWithUUID);
			} else {
				getOperation = createOperation(resourceHandler, "get", resourceName, resourceParentName,
						OperationEnum.getSubresourceWithUUID);
			}
		}

		if (getOperation != null) {
			path.setGet(getOperation);
		}
		return path;
	}

	private PathItem buildCreatePath(PathItem path,
								 DelegatingResourceHandler<?> resourceHandler, String resourceName, String resourceParentName) {
		io.swagger.v3.oas.models.Operation postCreateOperation = null;

		if (resourceParentName == null) {
			if (testOperationImplemented(OperationEnum.postCreate, resourceHandler)) {
				postCreateOperation = createOperation(resourceHandler, "post", resourceName, null,
						OperationEnum.postCreate);
			}
		} else {
			if (testOperationImplemented(OperationEnum.postSubresource, resourceHandler)) {
				postCreateOperation = createOperation(resourceHandler, "post", resourceName, resourceParentName,
						OperationEnum.postSubresource);
			}
		}

		if (postCreateOperation != null) {
			path.setPost(postCreateOperation);
		}
		return path;
	}

	private PathItem buildUpdatePath(PathItem path,
								 DelegatingResourceHandler<?> resourceHandler, String resourceName, String resourceParentName) {

		io.swagger.v3.oas.models.Operation postUpdateOperation = null;

		if (resourceParentName == null) {
			if (testOperationImplemented(OperationEnum.postUpdate, resourceHandler)) {
				postUpdateOperation = createOperation(resourceHandler, "post", resourceName, resourceParentName,
						OperationEnum.postUpdate);
			}
		} else {
			if (testOperationImplemented(OperationEnum.postUpdateSubresouce, resourceHandler)) {
				postUpdateOperation = createOperation(resourceHandler, "post", resourceName, resourceParentName,
						OperationEnum.postUpdateSubresouce);
			}
		}

		if (postUpdateOperation != null) {
			path.setPost(postUpdateOperation);
		}
		return path;
	}

	private PathItem buildDeletePath(PathItem path,
								 DelegatingResourceHandler<?> resourceHandler, String resourceName, String resourceParentName) {

		io.swagger.v3.oas.models.Operation deleteOperation = null;

		if (resourceParentName == null) {
			if (testOperationImplemented(OperationEnum.delete, resourceHandler)) {
				deleteOperation = createOperation(resourceHandler, "delete", resourceName, resourceParentName,
						OperationEnum.delete);
			}
		} else {
			if (testOperationImplemented(OperationEnum.deleteSubresource, resourceHandler)) {
				deleteOperation = createOperation(resourceHandler, "delete", resourceName, resourceParentName,
						OperationEnum.deleteSubresource);
			}
		}

		if (deleteOperation != null) {
			path.setDelete(deleteOperation);
		}
		return path;
	}

	private PathItem buildPurgePath(PathItem path, DelegatingResourceHandler<?> resourceHandler,
								String resourceName, String resourceParentName) {

		if (path.getDelete() != null) {
			// just add optional purge parameter
			io.swagger.v3.oas.models.Operation deleteOperation =  path.getDelete();

			deleteOperation.setSummary("Delete or purge resource by uuid");
			deleteOperation.setDescription("The resource will be voided/retired unless purge = 'true'");

			Parameter purgeParam = new Parameter().name("purge").in("query").schema(new StringSchema());
			deleteOperation.addParametersItem(purgeParam);
		} else {
			// create standalone purge operation with required
			io.swagger.v3.oas.models.Operation purgeOperation = null;

			if (resourceParentName == null) {
				if (testOperationImplemented(OperationEnum.purge, resourceHandler)) {
					purgeOperation = createOperation(resourceHandler, "delete", resourceName, null,
							OperationEnum.purge);
				}
			} else {
				if (testOperationImplemented(OperationEnum.purgeSubresource, resourceHandler)) {
					purgeOperation = createOperation(resourceHandler, "delete", resourceName, resourceParentName,
							OperationEnum.purgeSubresource);
				}
			}

			if (purgeOperation != null) {
				path.setDelete(purgeOperation);
			}
		}

		return path;
	}

	private void addIndividualPath(String resourceParentName, String resourceName, PathItem path,
								   String pathSuffix) {
		if (path.getGet() != null || path.getPost() != null || path.getDelete() != null) {
			String fullPath = resourceParentName == null ?
					"/" + resourceName + pathSuffix :
					"/" + resourceParentName + "/{parent-uuid}/" + resourceName + pathSuffix;

			log.debug("Adding path: " + fullPath);
			openAPI.getPaths().addPathItem(fullPath, path);
		}
	}

	private String buildSearchParameterDependencyString(Set<SearchParameter> dependencies) {
		StringBuffer sb = new StringBuffer();

		sb.append("Must be used with ");

		List<String> searchParameterNames = new ArrayList<String>();
		for (SearchParameter dependency : dependencies) {
			searchParameterNames.add(dependency.getName());
		}
		sb.append(StringUtils.join(searchParameterNames, ", "));

		String ret = sb.toString();
		int ind = ret.lastIndexOf(", ");

		if (ind > -1) {
			ret = new StringBuilder(ret).replace(ind, ind + 2, " and ").toString();
		}

		return ret;
	}

	private void addSearchOperations(DelegatingResourceHandler<?> resourceHandler, String resourceName,
									 String resourceParentName, PathItem getAllPath) {
		if (resourceName == null) {
			return;
		}
		boolean hasDoSearch = testOperationImplemented(OperationEnum.getWithDoSearch, resourceHandler);
		boolean hasSearchHandler = hasSearchHandler(resourceName, resourceParentName);
		boolean wasNew = false;

		if (hasSearchHandler || hasDoSearch) {
			io.swagger.v3.oas.models.Operation operation;
			// query parameter
			Parameter q = new Parameter()
					.name("q")
					.in("query")
					.description("The search query")
					.schema(new StringSchema());

			if (getAllPath.getGet() == null) {
				// create search-only operation
				operation = new io.swagger.v3.oas.models.Operation();
				operation.addTagsItem(resourceParentName == null ? resourceName : resourceParentName);
				
				// Set the responses using the proper method
				ApiResponses responses = new ApiResponses();
				responses.addApiResponse("200", new ApiResponse()
					.description(resourceName + " response")
					.content(new Content()
						.addMediaType("application/json", 
							new MediaType().schema(new Schema<ObjectSchema>().$ref("#/components/schemas/FetchAll")))));
				operation.setResponses(responses);

				// if the path has no operations, add a note that at least one search parameter must be specified
				operation.setSummary("Search for " + resourceName);
				operation.setDescription("At least one search parameter must be specified");

				// representations query parameter
				Parameter v = new QueryParameter().name("v")
						.description("The representation to return (ref, default, full or custom)")
						.schema(new StringSchema()
						._enum(Arrays.asList("ref", "default", "full", "custom")));

				// This implies that the resource has no custom SearchHandler or doGetAll, but has doSearch implemented
				// As there is only one query param 'q', mark it as required
				if (!hasSearchHandler) {
					q.setRequired(true);
				}

				operation.setParameters(buildPagingParameters());
				operation.addParametersItem(v).addParametersItem(q);
				if (((BaseDelegatingResource<?>) resourceHandler).hasTypesDefined()) {
					operation.addParametersItem(subclassTypeParameter);
				}
				// since the path has no existing get operations then it is considered new
				wasNew = true;
			} else {
				operation = getAllPath.getGet();
				operation.setSummary("Fetch all non-retired " + resourceName + " resources or perform search");
				operation.setDescription("All search parameters are optional");
				operation.addParametersItem(q);
			}

			Map<String, Parameter> parameterMap = new HashMap<String, Parameter>();

			if (hasSearchHandler) {
				// FIXME: this isn't perfect, it doesn't cover the case where multiple parameters are required together
				// FIXME: See https://github.com/OAI/OpenAPI-Specification/issues/256
				for (SearchHandler searchHandler : Context.getService(RestService.class).getAllSearchHandlers()) {

					String supportedResourceWithVersion = searchHandler.getSearchConfig().getSupportedResource();
					String supportedResource = supportedResourceWithVersion.substring(supportedResourceWithVersion
							.indexOf('/') + 1);

					if (resourceName.equals(supportedResource)) {
						for (SearchQuery searchQuery : searchHandler.getSearchConfig().getSearchQueries()) {
							// parameters with no dependencies
							for (SearchParameter requiredParameter : searchQuery.getRequiredParameters()) {
								Parameter p = new Parameter().in("query").schema(new StringSchema());
								p.setName(requiredParameter.getName());
								parameterMap.put(requiredParameter.getName(), p);
							}
							// parameters with dependencies
							for (SearchParameter optionalParameter : searchQuery.getOptionalParameters()) {
								Parameter p = new Parameter().in("query").schema(new StringSchema());
								p.setName(optionalParameter.getName());
								p.setDescription(buildSearchParameterDependencyString(searchQuery.getRequiredParameters()));
								parameterMap.put(optionalParameter.getName(), p);
							}
						}
					}
				}
			}

			for (Parameter p : parameterMap.values()) {
				operation.addParametersItem(p);
			}
			operation.setOperationId("getAll" + getOperationTitle(resourceHandler, true));

			if (wasNew) {
				getAllPath.setGet(operation);
			}
		}
	}

	private void addPaths() {
		log.debug("Starting addPaths method");
		
		if (openAPI.getPaths() == null) {
			log.debug("Paths object is null, initializing it");
			openAPI.setPaths(new Paths());
		}

		// get all registered resource handlers
		List<DelegatingResourceHandler<?>> resourceHandlers = Context.getService(RestService.class).getResourceHandlers();
		log.debug("Number of resource handlers: " + resourceHandlers.size());
		
		sortResourceHandlers(resourceHandlers);

		// generate swagger JSON for each handler
		for (DelegatingResourceHandler<?> resourceHandler : resourceHandlers) {
			try {
				// get name and parent if it's a subresource
				Resource annotation = resourceHandler.getClass().getAnnotation(Resource.class);

				String resourceParentName = null;
				String resourceName = null;

				if (annotation != null) {
					// top level resource
					resourceName = annotation.name().substring(annotation.name().indexOf('/') + 1);
				} else {
					// subresource
					SubResource subResourceAnnotation = resourceHandler.getClass().getAnnotation(SubResource.class);

					if (subResourceAnnotation != null) {
						Resource parentResourceAnnotation = subResourceAnnotation.parent().getAnnotation(Resource.class);

						resourceName = subResourceAnnotation.path();
						resourceParentName = parentResourceAnnotation.name().substring(
								parentResourceAnnotation.name().indexOf('/') + 1);
					}
				}

				log.debug("Processing resource: " + resourceName + (resourceParentName != null ? " (parent: " + resourceParentName + ")" : ""));

				// subclass operations are handled separately in another method
				if (resourceHandler instanceof DelegatingSubclassHandler) {
					log.debug("Skipping subclass handler for: " + resourceName);
					continue;
				}

				// set up paths
				PathItem rootPath = new PathItem();
				PathItem uuidPath = new PathItem();

				/////////////////////////
				// GET all             //
				/////////////////////////
				PathItem rootPathGetAll = buildFetchAllPath(rootPath, resourceHandler, resourceName,
						resourceParentName);
				addIndividualPath(resourceParentName, resourceName, rootPathGetAll, "");

				/////////////////////////
				// GET search          //
				/////////////////////////
				addSearchOperations(resourceHandler, resourceName, resourceParentName, rootPathGetAll);

				/////////////////////////
				// POST create         //
				/////////////////////////
				PathItem rootPathPostCreate = buildCreatePath(rootPathGetAll, resourceHandler, resourceName,
						resourceParentName);
				addIndividualPath(resourceParentName, resourceName, rootPathPostCreate, "");

				/////////////////////////
				// GET with UUID       //
				/////////////////////////
				PathItem uuidPathGetAll = buildGetWithUUIDPath(uuidPath, resourceHandler, resourceName,
						resourceParentName);
				addIndividualPath(resourceParentName, resourceName, uuidPathGetAll, "/{uuid}");

				/////////////////////////
				// POST update         //
				/////////////////////////
				PathItem uuidPathPostUpdate = buildUpdatePath(uuidPathGetAll, resourceHandler, resourceName,
						resourceParentName);
				addIndividualPath(resourceParentName, resourceName, uuidPathPostUpdate, "/{uuid}");

				/////////////////////////
				// DELETE              //
				/////////////////////////
				PathItem uuidPathDelete = buildDeletePath(uuidPathPostUpdate, resourceHandler, resourceName,
						resourceParentName);

				/////////////////////////
				// DELETE (purge)      //
				/////////////////////////
				PathItem uuidPathPurge = buildPurgePath(uuidPathDelete, resourceHandler, resourceName,
						resourceParentName);
				addIndividualPath(resourceParentName, resourceName, uuidPathPurge, "/{uuid}");

				// After building all paths for a resource, log the number of operations
				log.debug("Added " + (rootPath.readOperations().size() + uuidPath.readOperations().size()) + " operations for resource: " + resourceName);
			} catch (Exception e) {
				log.error("Error processing resource handler: " + resourceHandler.getClass().getName(), e);
			}
		}

		// After processing all resources, log the total number of paths
		log.debug("Finished addPaths method. Total paths: " + (openAPI.getPaths() != null ? openAPI.getPaths().size() : 0));
	}

	private String createJSON() {
		return Json.pretty(openAPI);
	}

	private Parameter buildRequiredUUIDParameter(String name, String desc) {
		return new Parameter().name(name).description(desc).schema(new StringSchema()).in("path").required(true);
	}

	private List<Parameter> buildPagingParameters() {
		List<Parameter> params = new ArrayList<Parameter>();

		Parameter limit = new Parameter().name("limit")
				.description("The number of results to return").schema(new IntegerSchema()).in("query");

		Parameter startIndex = new Parameter().name("startIndex")
				.description("The offset at which to start").schema(new IntegerSchema()).in("query");

		params.add(limit);
		params.add(startIndex);

		return params;
	}

	private Parameter buildPOSTBodyParameter(String resourceName, String resourceParentName,
											 OperationEnum operationEnum) {
		Parameter bodyParameter = new Parameter().name("resource").description("Resource to create").in("body").required(true);

		switch (operationEnum) {
			case postCreate:
			case postSubresource:
				bodyParameter.setDescription("Resource to create");
				break;
			case postUpdate:
			case postUpdateSubresouce:
				bodyParameter.setDescription("Resource properties to update");
		}

		bodyParameter.setSchema(new Schema<ObjectSchema>().$ref(getSchemaRef(resourceName, resourceParentName, operationEnum)));

		return bodyParameter;
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
		StringBuilder ret = new StringBuilder();
		for (String s : split) {
			ret.append(StringUtils.capitalize(s));
		}

		return ret.toString();
	}

	private String getSchemaRef(String resourceName, String resourceParentName, OperationEnum operationEnum) {
		return "#/components/schemas/" + getSchemaName(resourceName, resourceParentName, operationEnum);
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

	/**
	 * Creates and adds schema definitions to the OpenAPI components for a given resource and operation.
	 *
	 * @param operationEnum The type of operation (GET, CREATE, UPDATE)
	 * @param resourceName The name of the resource
	 * @param resourceParentName The name of the parent resource (if applicable, can be null)
	 * @param resourceHandler The DelegatingResourceHandler for the resource
	 *
	 * @throws IllegalArgumentException if operationEnum, resourceName, or resourceHandler is null
	 * <p>
	 * This method performs the following tasks:
	 * 1. Generates a schema name based on the resource and operation type
	 * 2. Retrieves or creates the Components object from the OpenAPI specification
	 * 3. Based on the operation type (Get, Create, or Update):
	 *    - Retrieves the appropriate schema(s) from the resourceHandler
	 *    - Adds the schema(s) to the Components object with the generated name
	 * <p>
	 * For GET operations, it adds schemas for DEFAULT, REF, and FULL representations.
	 * For CREATE operations, it adds schemas for DEFAULT and FULL representations.
	 * For UPDATE operations, it adds a schema for the DEFAULT representation.
	 *
	 * The method ensures that only non-null schemas are added to the components.
	 */
	private void createDefinition(OperationEnum operationEnum, String resourceName, String resourceParentName,
								  DelegatingResourceHandler<?> resourceHandler) {
		if (operationEnum == null || resourceName == null || resourceHandler == null) {
			throw new IllegalArgumentException("Operation, resource name, and resource handler must not be null");
		}

		String definitionName = getSchemaName(resourceName, resourceParentName, operationEnum);
		Components components = openAPI.getComponents();
		if (components == null) {
			components = new Components();
			openAPI.setComponents(components);
		}

		if (definitionName.endsWith("Get")) {
			Schema<?> getSchema = resourceHandler.getGETSchema(Representation.DEFAULT);
			Schema<?> getRefSchema = resourceHandler.getGETSchema(Representation.REF);
			Schema<?> getFullSchema = resourceHandler.getGETSchema(Representation.FULL);

			if (getSchema != null) {
				components.addSchemas(definitionName, getSchema);
			}
			if (getRefSchema != null) {
				components.addSchemas(definitionName + "Ref", getRefSchema);
			}
			if (getFullSchema != null) {
				components.addSchemas(definitionName + "Full", getFullSchema);
			}
		} else if (definitionName.endsWith("Create")) {
			Schema<?> createSchema = resourceHandler.getCREATESchema(Representation.DEFAULT);
			Schema<?> createFullSchema = resourceHandler.getCREATESchema(Representation.FULL);

			if (createSchema != null) {
				components.addSchemas(definitionName, createSchema);
			}
			if (createFullSchema != null) {
				components.addSchemas(definitionName + "Full", createFullSchema);
			}
		} else if (definitionName.endsWith("Update")) {
			Schema<?> updateSchema = resourceHandler.getUPDATESchema(Representation.DEFAULT);
			if (updateSchema != null) {
				components.addSchemas(definitionName, updateSchema);
			}
		}

	}

	/**
	 * Creates an OpenAPI Operation object for a given resource and operation type.
	 *
	 * @param resourceHandler The DelegatingResourceHandler for the resource
	 * @param operationName The name of the operation (e.g., "get", "post")
	 * @param resourceName The name of the resource
	 * @param resourceParentName The name of the parent resource (if applicable, null otherwise)
	 * @param operationEnum The type of operation (from OperationEnum)
	 * @return An OpenAPI Operation object describing the API endpoint
	 */
	private Operation createOperation(DelegatingResourceHandler<?> resourceHandler, String operationName,
									  String resourceName, String resourceParentName, OperationEnum operationEnum) {

		Operation operation = new Operation()
				.addTagsItem(resourceParentName == null ? resourceName : resourceParentName);

		// create definition
		if (operationName.equals("post") || operationName.equals("get")) {
			createDefinition(operationEnum, resourceName, resourceParentName, resourceHandler);
		}

		// create all possible responses
		ApiResponses responses = new ApiResponses();

		// 200 response (Successful operation)
		ApiResponse response200 = new ApiResponse().description(resourceName + " response");

		// 201 response (Successfully created)
		ApiResponse response201 = new ApiResponse().description(resourceName + " response");

		// 204 delete success
		ApiResponse response204 = new ApiResponse().description("Delete successful");

		// 401 response (User not logged in)
		ApiResponse response401 = new ApiResponse().description("User not logged in");

		// 404 (Object with given uuid doesn't exist)
		ApiResponse response404 = new ApiResponse().description("Resource with given uuid doesn't exist");

		// create all possible query params
		// representations query parameter
		Parameter v = new QueryParameter()
				.name("v")
				.description("The representation to return (ref, default, full or custom)")
				.schema(new StringSchema()._enum(Arrays.asList("ref", "default", "full", "custom")));

		switch (operationEnum) {
			case get:
				operation.summary("Fetch all non-retired")
						.operationId("getAll" + getOperationTitle(resourceHandler, true));
				response200.content(new Content().addMediaType("application/json",
						new MediaType().schema(new ArraySchema().items(new Schema<Object>().$ref(getSchemaRef(resourceName, resourceParentName, OperationEnum.get))))));
				responses.addApiResponse("200", response200);

				operation.parameters(buildPagingParameters());
				operation.addParametersItem(v);
				if (((BaseDelegatingResource<?>) resourceHandler).hasTypesDefined()) {
					operation.addParametersItem(subclassTypeParameter);
				}
				break;

			case getWithUUID:
				operation.summary("Fetch by uuid")
						.operationId("get" + getOperationTitle(resourceHandler, false));
				operation.addParametersItem(v);
				operation.addParametersItem(buildRequiredUUIDParameter("uuid", "uuid to filter by"));
				response200.content(new Content().addMediaType("application/json",
						new MediaType().schema(new Schema<Object>().$ref(getSchemaRef(resourceName, resourceParentName, OperationEnum.get)))));
				responses.addApiResponse("200", response200);
				responses.addApiResponse("404", response404);
				break;

			case postCreate:
				operation.summary("Create with properties in request")
						.operationId("create" + getOperationTitle(resourceHandler, false));
				operation.addParametersItem(buildPOSTBodyParameter(resourceName, resourceParentName, OperationEnum.postCreate));
				responses.addApiResponse("201", response201);
				break;

			case postUpdate:
				operation.summary("Edit with given uuid, only modifying properties in request")
						.operationId("update" + getOperationTitle(resourceHandler, false));
				operation.addParametersItem(buildRequiredUUIDParameter("uuid", "uuid of resource to update"));
				operation.addParametersItem(buildPOSTBodyParameter(resourceName, resourceParentName, OperationEnum.postUpdate));
				responses.addApiResponse("201", response201);
				break;

			case getSubresource:
				operation.summary("Fetch all non-retired " + resourceName + " subresources")
						.operationId("getAll" + getOperationTitle(resourceHandler, true));
				operation.parameters(buildPagingParameters());
				operation.addParametersItem(buildRequiredUUIDParameter("parent-uuid", "parent resource uuid"));
				operation.addParametersItem(v);
				response200.content(new Content().addMediaType("application/json",
						new MediaType().schema(new ObjectSchema()
								.addProperty("results", new ArraySchema()
										.items(new Schema<Object>().$ref(getSchemaRef(resourceName, resourceParentName, OperationEnum.get)))))));
				responses.addApiResponse("200", response200);
				break;

			case postSubresource:
				operation.summary("Create " + resourceName + " subresource with properties in request")
						.operationId("create" + getOperationTitle(resourceHandler, false));
				operation.addParametersItem(buildRequiredUUIDParameter("parent-uuid", "parent resource uuid"));
				operation.addParametersItem(buildPOSTBodyParameter(resourceName, resourceParentName, OperationEnum.postSubresource));
				responses.addApiResponse("201", response201);
				break;

			case postUpdateSubresouce:
				operation.summary("edit " + resourceName + " subresource with given uuid, only modifying properties in request")
						.operationId("update" + getOperationTitle(resourceHandler, false));
				operation.addParametersItem(buildRequiredUUIDParameter("parent-uuid", "parent resource uuid"));
				operation.addParametersItem(buildRequiredUUIDParameter("uuid", "uuid of resource to update"));
				operation.addParametersItem(buildPOSTBodyParameter(resourceName, resourceParentName, OperationEnum.postUpdateSubresouce));
				responses.addApiResponse("201", response201);
				break;

			case getSubresourceWithUUID:
				operation.summary("Fetch " + resourceName + " subresources by uuid")
						.operationId("get" + getOperationTitle(resourceHandler, false));
				operation.addParametersItem(buildRequiredUUIDParameter("parent-uuid", "parent resource uuid"));
				operation.addParametersItem(buildRequiredUUIDParameter("uuid", "uuid to filter by"));
				operation.addParametersItem(v);
				response200.content(new Content().addMediaType("application/json",
						new MediaType().schema(new Schema<Object>().$ref(getSchemaRef(resourceName, resourceParentName, OperationEnum.getSubresourceWithUUID)))));
				responses.addApiResponse("200", response200);
				responses.addApiResponse("404", response404);
				break;

			case delete:
				operation.summary("Delete resource by uuid")
						.operationId("delete" + getOperationTitle(resourceHandler, false));
				operation.addParametersItem(buildRequiredUUIDParameter("uuid", "uuid to delete"));
				responses.addApiResponse("204", response204);
				responses.addApiResponse("404", response404);
				break;

			case deleteSubresource:
				operation.summary("Delete " + resourceName + " subresource by uuid")
						.operationId("delete" + getOperationTitle(resourceHandler, false));
				operation.addParametersItem(buildRequiredUUIDParameter("parent-uuid", "parent resource uuid"));
				operation.addParametersItem(buildRequiredUUIDParameter("uuid", "uuid to delete"));
				responses.addApiResponse("204", response204);
				responses.addApiResponse("404", response404);
				break;

			case purge:
				operation.summary("Purge resource by uuid")
						.operationId("purge" + getOperationTitle(resourceHandler, false));
				operation.addParametersItem(buildRequiredUUIDParameter("uuid", "uuid to delete"));
				responses.addApiResponse("204", response204);
				break;

			case purgeSubresource:
				operation.summary("Purge " + resourceName + " subresource by uuid")
						.operationId("purge" + getOperationTitle(resourceHandler, false));
				operation.addParametersItem(buildRequiredUUIDParameter("parent-uuid", "parent resource uuid"));
				operation.addParametersItem(buildRequiredUUIDParameter("uuid", "uuid to delete"));
				responses.addApiResponse("204", response204);
				break;
		}

		responses.addApiResponse("401", response401);
		operation.responses(responses);

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
		//Set the root url.
		return baseUrl + "/v1/" + resourceName;
	}

	public boolean hasSearchHandler(String resourceName, String resourceParentName) {
		if (resourceParentName != null) {
			resourceName = RestConstants.VERSION_1 + "/" + resourceParentName + "/" + resourceName;
		} else {
			resourceName = RestConstants.VERSION_1 + "/" + resourceName;
		}

		List<SearchHandler> searchHandlers = Context.getService(RestService.class).getAllSearchHandlers();
		for (SearchHandler searchHandler : searchHandlers) {
			if (searchHandler.getSearchConfig().getSupportedResource().equals(resourceName)) {
				return true;
			}
		}
		return false;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public OpenAPI getOpenAPI() {
		if (openAPI == null) {
			log.debug("OpenAPI object is null, creating new specification");
			BuildJSON();
		}
		return openAPI;
	}

	/**
	 * @return true if and only if openAPI is not null, and its paths are also set.
	 */
	public static boolean isCached() {
		return openAPI != null &&
				openAPI.getPaths() != null &&
				!openAPI.getPaths().isEmpty();
	}

	public static void clearCache() {
		openAPI = null;
		cachedJson = null;
	}

}