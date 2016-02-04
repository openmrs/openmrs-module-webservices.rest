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

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.docs.ResourceDoc;
import org.openmrs.module.webservices.docs.ResourceRepresentation;
import org.openmrs.module.webservices.docs.SearchHandlerDoc;
import org.openmrs.module.webservices.docs.SearchQueryDoc;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.Converter;
import org.openmrs.module.webservices.rest.web.resource.api.Listable;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription.Property;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubclassHandler;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.Map.Entry;

public class SwaggerSpecificationCreator {
	
	private SwaggerSpecification swaggerSpecification;
	
	private String baseUrl;
	
	private static List<ResourceDoc> resourceDocList = new ArrayList<ResourceDoc>();
	
	private static List<SearchHandlerDoc> searchHandlerDocs;
	
	public SwaggerSpecificationCreator(String baseUrl) {
		this.swaggerSpecification = new SwaggerSpecification();
		this.baseUrl = baseUrl;
		List<SearchHandler> searchHandlers = Context.getService(RestService.class).getAllSearchHandlers();
		searchHandlerDocs = fillSearchHandlers(searchHandlers, baseUrl);
	}
	
	public String BuildJSON() {
		synchronized (this) {
			CreateApiDefinition();
			AddPaths();
			CreateObjectDefinitions();
			AddResourceTags();
		}
		return CreateJSON();
	}
	
	private void CreateApiDefinition() {
		Info info = new Info();
		info.setVersion("1.0.0");
		info.setTitle("OpenMRS Rest Services");
		info.setDescription("auto-generated documentation for OpenMRS Rest services");
		Contact contact = new Contact();
		contact.setName("OpenMRS Rest Module Team");
		License license = new License();
		license.setName("MIT");
		info.setContact(null);
		info.setLicense(null);
		swaggerSpecification.setInfo(info);
		List<String> produces = new ArrayList<String>();
		produces.add("application/json");
		List<String> consumes = new ArrayList<String>();
		consumes.add("application/json");
		swaggerSpecification.setHost(getBaseUrl());
		swaggerSpecification.setBasePath("/" + RestConstants.VERSION_1);
		swaggerSpecification.setProduces(produces);
		swaggerSpecification.setConsumes(consumes);
	}
	
	private RequestContext BuildDummyRequestContextForOperationTest(OperationEnum operation) {
		RequestContext rc = new RequestContext();
		rc.setRequest(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
		
		switch (operation) {
			case get:
				rc.setLimit(1);
				
		}
		
		return rc;
	}
	
	private boolean TestOperationImplemented(OperationEnum operation, BaseDelegatingResource resource) {
		switch (operation) {
			case get:
				try {
					((Listable) resource).getAll(BuildDummyRequestContextForOperationTest(OperationEnum.get));
					return true;
				}
				catch (ResourceDoesNotSupportOperationException e) {
					return false;
				}
		}
		return true;
	}
	
	private void AddPaths() {
		List<DelegatingResourceHandler<?>> resourceHandlers = Context.getService(RestService.class).getResourceHandlers();
		Paths paths = new Paths();
		
		Map<String, Path> pathMap = new HashMap<String, Path>();
		
		Collections.sort(resourceHandlers, new Comparator<DelegatingResourceHandler<?>>() {
			
			@Override
			public int compare(DelegatingResourceHandler<?> left, DelegatingResourceHandler<?> right) {
				return isSubclass(left).compareTo(isSubclass(right));
			}
			
			private Boolean isSubclass(DelegatingResourceHandler<?> resourceHandler) {
				return resourceHandler.getClass().getAnnotation(
				    org.openmrs.module.webservices.rest.web.annotation.SubResource.class) != null;
			}
		});
		
		for (DelegatingResourceHandler<?> resourceHandler : resourceHandlers) {
			
			Object delegate = null;
			try {
				if (!Modifier.isInterface(resourceHandler.newDelegate().getClass().getModifiers())
				        && !Modifier.isAbstract(resourceHandler.newDelegate().getClass().getModifiers())) {
					delegate = resourceHandler.newDelegate();
				}
			}
			catch (Exception ex) {
				continue;
			}
			if (delegate == null) {
				// TODO: handle resources that don't implement newDelegate(), e.g. ConceptSearchResource1_9, all subclasses of EvaluatedResource in the reporting rest module
				continue;
			}
			
			String resourceClassname = delegate.getClass().getSimpleName();
			if (resourceClassname.equals("UserAndPassword1_8")) {
				resourceClassname = "User"; //Work-around for UserAndPassword to be displayed as User
			} else if (resourceClassname.equals("CohortMember1_8")) {
				resourceClassname = "CohortMember";
			} else if (resourceClassname.equals("IncomingHl7Message1_8")) {
				resourceClassname = "HL7";
			}
			
			String subResourceForClass = null;
			ResourceDoc resourceDoc = new ResourceDoc(resourceClassname);
			resourceDoc.setResourceVersion(resourceHandler.getResourceVersion());
			org.openmrs.module.webservices.rest.web.annotation.Resource resourceAnnotation = ((org.openmrs.module.webservices.rest.web.annotation.Resource) resourceHandler
			        .getClass().getAnnotation(org.openmrs.module.webservices.rest.web.annotation.Resource.class));
			if (resourceAnnotation != null) {
				resourceDoc.setResourceName(resourceAnnotation.name());
				
				String[] supportedVersions = resourceAnnotation.supportedOpenmrsVersions();
				List<String> supportedVersionsList = new ArrayList<String>();
				
				for (String version : supportedVersions) {
					supportedVersionsList.add(version);
				}
				
				resourceDoc.setSupportedOpenMRSVersion(supportedVersionsList);
				
			} else {
				//this is a subResource, use the name of the collection
				org.openmrs.module.webservices.rest.web.annotation.SubResource subResourceAnnotation = ((org.openmrs.module.webservices.rest.web.annotation.SubResource) resourceHandler
				        .getClass().getAnnotation(org.openmrs.module.webservices.rest.web.annotation.SubResource.class));
				if (subResourceAnnotation != null) {
					org.openmrs.module.webservices.rest.web.annotation.Resource parentResourceAnnotation = ((org.openmrs.module.webservices.rest.web.annotation.Resource) subResourceAnnotation
					        .parent().getAnnotation(org.openmrs.module.webservices.rest.web.annotation.Resource.class));
					
					resourceDoc.setResourceName(parentResourceAnnotation.name());
					resourceDoc.setSubResourceName(subResourceAnnotation.path());
					
					subResourceForClass = parentResourceAnnotation.supportedClass().getSimpleName();
				}
			}
			
			Object instance = resourceHandler;
			
			//GET representations
			Representation[] representations = new Representation[] { Representation.REF, Representation.DEFAULT,
			        Representation.FULL };
			
			for (Representation representation : representations) {
				if (instance instanceof Converter) {
					try {
						@SuppressWarnings("unchecked")
						Converter<Object> converter = (Converter<Object>) instance;
						SimpleObject simpleObject = converter.asRepresentation(delegate, representation);
						resourceDoc.addRepresentation(new ResourceRepresentation(
						        "GET " + representation.getRepresentation(), simpleObject.keySet()));
					}
					catch (Exception e) {
						resourceDoc.addRepresentation(new ResourceRepresentation(
						        "GET " + representation.getRepresentation(), Arrays.asList("Not supported")));
					}
				} else {
					resourceDoc.addRepresentation(new ResourceRepresentation("GET " + representation.getRepresentation(),
					        Arrays.asList("Not supported")));
				}
			}
			
			//POST create representations
			try {
				DelegatingResourceDescription description = resourceHandler.getCreatableProperties();
				List<String> properties = getPOSTProperties(description);
				resourceDoc.addRepresentation(new ResourceRepresentation("POST create", properties));
			}
			catch (ResourceDoesNotSupportOperationException e) {
				resourceDoc.addRepresentation(new ResourceRepresentation("POST create", Arrays.asList("Not supported")));
			}
			
			//POST update representations
			try {
				DelegatingResourceDescription description = resourceHandler.getUpdatableProperties();
				List<String> properties = getPOSTProperties(description);
				resourceDoc.addRepresentation(new ResourceRepresentation("POST update", properties));
			}
			catch (ResourceDoesNotSupportOperationException e) {
				resourceDoc.addRepresentation(new ResourceRepresentation("POST update", Arrays.asList("Not supported")));
			}
			
			Path path = new Path();
			Path path2 = new Path();
			Path path3 = new Path();
			Path path4 = new Path();
			Map<String, Operation> operationsMap = new HashMap<String, Operation>();
			Map<String, Operation> operationsWithUUIDMap = new HashMap<String, Operation>();
			
			for (ResourceRepresentation representation : resourceDoc.getRepresentations()) {
				String resourceLongName = resourceDoc.getResourceName();
				String resourceURL = resourceDoc.getUrl();
				if (resourceLongName != null) {
					String tempRepresentationName = representation.getName();
					String tempOperation = (tempRepresentationName.split(" "))[0];
					String operationType = (tempRepresentationName.split(" "))[1];
					
					String resourceName = (resourceLongName.split("/"))[1];
					
					//For Get Representation
					if (tempOperation.equals("GET")) {
						if (operationType.equals("full")) {
							//Get resource
							Operation operationGet = null;
							
							if (resourceDoc.isSubResource()) {
								operationGet = CreateOperation("get", resourceName, representation,
								    OperationEnum.getSubresource);
							} else {
								if (TestOperationImplemented(OperationEnum.get, (BaseDelegatingResource) instance)) {
									operationGet = CreateOperation("get", resourceName, representation, OperationEnum.get);
								}
							}
							
							if (operationGet != null) {
								operationsMap.put("get", operationGet);
								path.setOperations(operationsMap);
								if (resourceDoc.isSubResource()) {
									pathMap.put("/" + resourceName + "/{uuid}/" + resourceDoc.getSubResourceName(), path);
								} else {
									pathMap.put("/" + resourceName, path);
								}
								
							}
							if (HasSearchHandler(resourceName)) {
								List<Operation> searchHandlerOperations = CreateSearchHandlersOperations(resourceName);
								
								for (Operation operation : searchHandlerOperations) {
									Map<String, Operation> searchHandlerMap = new HashMap<String, Operation>();
									searchHandlerMap.put("get", operation);
									Path searchHandlerPath = new Path();
									searchHandlerPath.setOperations(searchHandlerMap);
									StringBuffer buffer = new StringBuffer();
									for (int i = 0; i < operation.getParameters().size(); i++) {
										buffer.append(operation.getParameters().get(i).getName());
										if (i != operation.getParameters().size() - 1) {
											buffer.append(",");
										}
									}
									pathMap.put("/" + resourceName + " (Search by parameters: " + buffer.toString() + ")",
									    searchHandlerPath);
								}
							}
							
							//Get resource/{uuid} 
							Operation operationGetWithUUID = new Operation();
							operationGetWithUUID = CreateOperation("get", resourceName, representation,
							    OperationEnum.getWithUUID);
							
							if (operationGetWithUUID != null) {
								operationsWithUUIDMap.put("get", operationGetWithUUID);
								path2.setOperations(operationsWithUUIDMap);
								
								pathMap.put("/" + resourceName + "/{uuid}", path2);
								
							}
							
						}
					}// For Post Representation
					else {
						//Post create
						if (operationType.equals("create")) {
							Operation operationPostCreate = new Operation();
							if (resourceDoc.isSubResource())
								operationPostCreate = CreateOperation("post", resourceName, representation,
								    OperationEnum.postSubresource);
							else
								operationPostCreate = CreateOperation("post", resourceName, representation,
								    OperationEnum.postCreate);
							
							if (operationPostCreate != null) {
								operationsMap.put("post", operationPostCreate);
								path3.setOperations(operationsMap);
								
								if (resourceDoc.isSubResource()) {
									pathMap.put("/" + resourceName + "/{uuid}/" + resourceDoc.getSubResourceName(), path3);
								} else {
									pathMap.put("/" + resourceName, path3);
								}
							}
							
						} else {
							//Post update
							Operation operationPostUpdate = new Operation();
							
							operationPostUpdate = CreateOperation("post", resourceName, representation,
							    OperationEnum.postUpdate);
							
							if (operationPostUpdate != null) {
								operationsWithUUIDMap.put("post", operationPostUpdate);
								path4.setOperations(operationsWithUUIDMap);
								
								pathMap.put("/" + resourceName + "/{uuid}", path4);
								
							}
						}
					}
				} else {
					//This is a subtype, do the same thing 
					String tempRepresentationName = representation.getName();
					String tempOperation = (tempRepresentationName.split(" "))[0];
					String operationType = (tempRepresentationName.split(" "))[1];
					
					String resourceName = resourceDoc.getName();
					
					//For Get Representation
					if (tempOperation.equals("GET")) {
						if (operationType.equals("full")) {
							//Get resource
							Operation operationGet = null;
							
							if (TestOperationImplemented(OperationEnum.get, (BaseDelegatingResource) instance)) {
								operationGet = CreateOperation("get", resourceName, representation, OperationEnum.get);
							}
							
							if (operationGet != null) {
								operationsMap.put("get", operationGet);
								path.setOperations(operationsMap);
								pathMap.put("/" + resourceName, path);
							}
							
							//Get resource/{uuid} 
							Operation operationGetWithUUID = new Operation();
							operationGetWithUUID = CreateOperation("get", resourceName, representation,
							    OperationEnum.getWithUUID);
							
							if (operationGetWithUUID != null) {
								operationsWithUUIDMap.put("get", operationGetWithUUID);
								path2.setOperations(operationsWithUUIDMap);
								
								pathMap.put("/" + resourceName + "/{uuid}", path2);
								
							}
							
						}
					}// For Post Representation
					else {
						//Post create
						if (operationType.equals("create")) {
							
							Operation operationPostCreate = CreateOperation("post", resourceName, representation,
							    OperationEnum.postCreate);
							
							if (operationPostCreate != null) {
								operationsMap.put("post", operationPostCreate);
								path3.setOperations(operationsMap);
								
								pathMap.put("/" + resourceName, path3);
							}
							
						} else {
							//Post update
							Operation operationPostUpdate = CreateOperation("post", resourceName, representation,
							    OperationEnum.postUpdate);
							
							if (operationPostUpdate != null) {
								operationsWithUUIDMap.put("post", operationPostUpdate);
								path4.setOperations(operationsWithUUIDMap);
								
								pathMap.put("/" + resourceName + "/{uuid}", path4);
							}
						}
					}
					
					Class<?> resourceClass = ((DelegatingSubclassHandler<?, ?>) resourceHandler).getSuperclass();
					instance = Context.getService(RestService.class).getResourceBySupportedClass(resourceClass);
					
					resourceDoc.setSubtypeHandlerForResourceName(resourceClass.getSimpleName());
					resourceDoc.addSubtypeHandler(new ResourceDoc(resourceDoc.getName()));
					
				}
				resourceDocList.add(resourceDoc);
			}
		}
		paths.setPaths(pathMap);
		swaggerSpecification.setPaths(paths);
	}
	
	private void CreateObjectDefinitions() {
		Definitions definitions = new Definitions();
		Map<String, Definition> definitionsMap = new HashMap<String, Definition>();
		
		for (ResourceDoc doc : resourceDocList) {
			String resourceLongName = doc.getResourceName();
			if (resourceLongName != null) {
				Definition definition = new Definition();
				definition.setType("object");
				Properties properties = new Properties();
				Map<String, DefinitionProperty> propertiesMap = new HashMap<String, DefinitionProperty>();
				String resourceName = (resourceLongName.split("/"))[1];
				//String resourceName = doc.getName();
				String resourceDefinitionName = resourceName;
				for (ResourceRepresentation representation : doc.getRepresentations()) {
					String tempRepresentationName = representation.getName();
					String tempOperation = (tempRepresentationName.split(" "))[0];
					String operationType = (tempRepresentationName.split(" "))[1];
					for (String representationProperty : representation.getProperties()) {
						DefinitionProperty property = new DefinitionProperty();
						//all properties are of type string
						if (!representationProperty.equals("Not supported")) {
							property.setType("string");
							String propertyNameWithoutStar = "";
							if (representationProperty.startsWith("*")) {
								propertyNameWithoutStar = representationProperty.replace("*", "");
							} else {
								propertyNameWithoutStar = representationProperty;
							}
							
							propertiesMap.put(propertyNameWithoutStar, property);
						}
					}
					//Definitions for POST CREATE and POST UPDATE
					if (!tempOperation.equals("GET")) {
						if (operationType.equals("create")) {
							resourceDefinitionName = resourceName + "CreateInput";
						} else {
							resourceDefinitionName = resourceName + "UpdateInput";
						}
						
					}
					properties.setProperties(propertiesMap);
					definition.setProperties(properties);
					definitionsMap.put(resourceDefinitionName, definition);
					
				}
				
			}
		}
		
		definitions.setDefinitions(definitionsMap);
		swaggerSpecification.setDefinitions(definitions);
	}
	
	/**
	 * @return the swaggerSpecification
	 */
	public SwaggerSpecification getSwaggerSpecification() {
		return swaggerSpecification;
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
	
	private List<Parameter> getParametersList(Collection<String> properties, String resourceName, OperationEnum operationEnum) {
		List<Parameter> parameters = new ArrayList<Parameter>();
		String resourceURL = getResourceUrl(getBaseUrl(), resourceName);
		if (operationEnum == OperationEnum.get) {
			for (String property : properties) {
				if (property.equals("Not supported")) {
					return null;
				}
			}
		} else if (operationEnum == OperationEnum.getWithUUID || operationEnum == OperationEnum.getSubresource) {
			for (String property : properties) {
				if (property.equals("Not supported")) {
					return null;
				}
			}
			Parameter parameter = new Parameter();
			parameter.setName("uuid");
			parameter.setIn("path");
			parameter.setDescription("uuid to filter by");
			parameter.setRequired(true);
			parameters.add(parameter);
		} else if (operationEnum == OperationEnum.postCreate) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("Parameters: ");
			for (String property : properties) {
				if (property.equals("Not supported")) {
					return null;
				}
				
				if (property.startsWith("*")) {
					
					String propertyStringWithoutStar = property.replace("*", "");
					buffer.append(propertyStringWithoutStar + "(required)");
				} else {
					buffer.append(property + "(optional)");
				}
				buffer.append(" ");
			}
			
			Parameter parameter = new Parameter();
			parameter.setName("Object to create");
			parameter.setIn("body");
			parameter.setDescription(buffer.toString());
			parameter.setRequired(true);
			parameters.add(parameter);
			
		} else if (operationEnum == OperationEnum.postSubresource) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("Parameters: ");
			for (String property : properties) {
				if (property.equals("Not supported")) {
					return null;
				}
				
				if (property.startsWith("*")) {
					
					String propertyStringWithoutStar = property.replace("*", "");
					buffer.append(propertyStringWithoutStar + "(required)");
				} else {
					buffer.append(property + "(optional)");
				}
				buffer.append(" ");
			}
			
			Parameter parameter = new Parameter();
			parameter.setName("Object to create");
			parameter.setIn("body");
			parameter.setDescription(buffer.toString());
			parameter.setRequired(true);
			parameters.add(parameter);
			
			Parameter parameter2 = new Parameter();
			parameter2.setName("uuid");
			parameter2.setIn("path");
			parameter2.setDescription("uuid to filter by");
			parameter2.setRequired(true);
			parameters.add(parameter2);
			
		} else if (operationEnum == OperationEnum.postUpdate) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("Parameters: ");
			for (String property : properties) {
				if (property.equals("Not supported")) {
					return null;
				}
				
				if (property.startsWith("*")) {
					
					String propertyStringWithoutStar = property.replace("*", "");
					buffer.append(propertyStringWithoutStar + "(required)");
				} else {
					buffer.append(property + "(optional)");
				}
				buffer.append(" ");
				
			}
			
			Parameter parameter = new Parameter();
			parameter.setName("uuid");
			parameter.setIn("path");
			parameter.setDescription("uuid of the resource to update");
			parameter.setRequired(true);
			parameters.add(parameter);
			
			Parameter parameter2 = new Parameter();
			
			parameter2.setName("Object to update");
			parameter2.setIn("body");
			parameter2.setRequired(true);
			parameter2.setDescription(buffer.toString());
			
			parameters.add(parameter2);
			
		} else if (operationEnum == OperationEnum.getSubresourceWithUUID) {
			for (String property : properties) {
				if (property.equals("Not supported")) {
					return null;
				}
			}
			Parameter parameter = new Parameter();
			parameter.setName("uuid");
			parameter.setIn("path");
			parameter.setDescription("uuid to filter by");
			parameter.setRequired(true);
			parameters.add(parameter);
			
			Parameter parameter2 = new Parameter();
			parameter2.setName("subresource-uuid");
			parameter2.setIn("path");
			parameter2.setDescription("subresource uuid to filter by");
			parameter2.setRequired(true);
			parameters.add(parameter2);
		} else if (operationEnum == OperationEnum.postUpdateSubresouce) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("Parameters: ");
			for (String property : properties) {
				if (property.equals("Not supported")) {
					return null;
				}
				
				if (property.startsWith("*")) {
					
					String propertyStringWithoutStar = property.replace("*", "");
					buffer.append(propertyStringWithoutStar + "(required)");
				} else {
					buffer.append(property + "(optional)");
				}
				buffer.append(" ");
				
			}
			
			Parameter parameter = new Parameter();
			parameter.setName("uuid");
			parameter.setIn("path");
			parameter.setDescription("uuid of the resource to update");
			parameter.setRequired(true);
			parameters.add(parameter);
			
			Parameter parameter2 = new Parameter();
			
			parameter2.setName("Object to update");
			parameter2.setIn("body");
			parameter2.setRequired(true);
			parameter2.setDescription(buffer.toString());
			
			parameters.add(parameter2);
			
			Parameter parameter3 = new Parameter();
			parameter3.setName("subresource-uuid");
			parameter3.setIn("path");
			parameter3.setRequired(true);
			parameter3.setDescription("subresource uuid to filter by");
			
			parameters.add(parameter3);
		}
		
		return parameters;
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
	
	private String CreateJSON() {
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
	
	private void AddResourceTags() {
		
		List<Tag> tags = new ArrayList<Tag>();
		for (ResourceDoc doc : resourceDocList) {
			String resourceLongName = doc.getResourceName();
			if (resourceLongName != null) {
				String resourceName = (resourceLongName.split("/"))[1];
				Tag tag = new Tag();
				tag.setName(resourceName);
				/* For now, we do not add any description */
				tag.setDescription("");
				tags.add(tag);
			} else {
				for (ResourceDoc subType : doc.getSubtypeHandlers()) {
					Tag subTypeTag = new Tag();
					subTypeTag.setName(subType.getName());
					
					subTypeTag.setDescription("subtype of  " + doc.getSubtypeHandlerForResourceName());
					tags.add(subTypeTag);
				}
				
			}
		}
		
		swaggerSpecification.setTags(tags);
	}
	
	private Operation CreateOperation(String operationName, String resourceName, ResourceRepresentation representation,
	        OperationEnum operationEnum) {
		
		Operation operation = new Operation();
		operation.setName(operationName);
		operation.setDescription(null);
		
		List<String> produces = new ArrayList<String>();
		produces.add("application/json");
		operation.setProduces(produces);
		List<Parameter> parameters = new ArrayList<Parameter>();
		
		parameters = getParametersList(representation.getProperties(), resourceName, operationEnum);
		
		if (parameters == null)
			return null;
		
		operation.setParameters(parameters);
		
		Response statusOKResponse = new Response();
		statusOKResponse.setDescription(resourceName + " response");
		Schema schema = new Schema();
		if (operationEnum == OperationEnum.get) {
			schema.setRef("#/definitions/" + resourceName);
			operation.setSummary("Fetch all non-retired");
		} else if (operationEnum == OperationEnum.getWithUUID) {
			schema.setRef("#/definitions/" + resourceName);
			operation.setSummary("Fetch by unique uuid");
		} else if (operationEnum == OperationEnum.postCreate) {
			schema.setRef("#/definitions/" + resourceName + "createInput");
			operation.setSummary("Create with properties in request");
		} else if (operationEnum == OperationEnum.postUpdate) {
			schema.setRef("#/definitions/" + resourceName + "updateInput");
			operation.setSummary("Edit with given uuid, only modifying properties in request");
		} else if (operationEnum == OperationEnum.getSubresource || operationEnum == OperationEnum.postSubresource
		        || operationEnum == OperationEnum.postUpdateSubresouce
		        || operationEnum == OperationEnum.getSubresourceWithUUID) {
			operation.setSummary("Subresource");
		}
		
		statusOKResponse.setSchema(schema);
		List<String> resourceTags = new ArrayList<String>();
		resourceTags.add(resourceName);
		operation.setTags(resourceTags);
		Map<String, Response> responses = new HashMap<String, Response>();
		responses.put("200", statusOKResponse);
		operation.setResponses(responses);
		
		return operation;
	}
	
	private Operation CreateSearchHandlerOperation(String operationName, String resourceName, String searchHandlerId,
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
	
	private boolean HasSearchHandler(String resourceName) {
		for (SearchHandlerDoc doc : searchHandlerDocs) {
			if (doc.getResourceURL().contains(resourceName)) {
				return true;
			}
		}
		
		return false;
	}
	
	private List<Operation> CreateSearchHandlersOperations(String resourceName) {
		List<Operation> searchHandlersOperations = new ArrayList<Operation>();
		
		for (SearchHandlerDoc doc : searchHandlerDocs) {
			String currentResourceName = doc.getResourceURL().replace(getBaseUrl() + "/v1/", "");
			if (currentResourceName.equals(resourceName)) {
				for (SearchQueryDoc queryDoc : doc.getSearchQueriesDoc()) {
					int queryIndex = doc.getSearchQueriesDoc().indexOf(queryDoc);
					Operation searchHandlerOperation = CreateSearchHandlerOperation("get", resourceName,
					    doc.getSearchHandlerId(), OperationEnum.getWithSearchHandler, queryIndex);
					searchHandlerOperation.setDescription(queryDoc.getDescription());
					searchHandlersOperations.add(searchHandlerOperation);
				}
			}
		}
		return searchHandlersOperations;
	}
	
	/**
	 * @return the baseUrl
	 */
	public String getBaseUrl() {
		return baseUrl;
	}
	
	/**
	 * @param baseUrl the baseUrl to set
	 */
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	private ResourceDoc findResource(String resourceName) {
		for (int i = 0; i < resourceDocList.size(); i++) {
			if (resourceDocList.get(i).getResourceName().equals(resourceName)) {
				return resourceDocList.get(i);
			}
		}
		return null;
	}
	
}
