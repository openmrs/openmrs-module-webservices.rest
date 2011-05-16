package org.openmrs.module.webservices.rest.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.PatientResource;
import org.openmrs.module.webservices.rest.web.resource.api.CrudResource;
import org.openmrs.module.webservices.rest.web.resource.api.Searchable;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

/**
 * Base controller that handles exceptions (via {@link BaseRestController}) and also standard CRUD
 * operations based on a {@link CrudResource}.
 * 
 * @param <R>
 */
public abstract class BaseCrudController<R extends CrudResource> extends BaseRestController {
	
	public abstract R getResource();
	
	/**
	 * @param uuid
	 * @param request
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
	@ResponseBody
	public Object retrieve(@PathVariable("uuid") String uuid, WebRequest request) throws ResponseException {
		RequestContext context = RestUtil.getRequestContext(request);
		R resource = getResource();
		return resource.retrieve(uuid, context);
	}
	
	/**
	 * @param post
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public Object create(@RequestBody SimpleObject post, WebRequest request, HttpServletResponse response)
	                                                                                                      throws ResponseException {
		RequestContext context = RestUtil.getRequestContext(request);
		Object created = getResource().create(post, context);
		return RestUtil.created(response, created);
	}
	
	/**
	 * @param uuid
	 * @param post
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/{uuid}", method = RequestMethod.POST)
	@ResponseBody
	public Object update(@PathVariable("uuid") String uuid, @RequestBody SimpleObject post, WebRequest request,
	                     HttpServletResponse response) throws ResponseException {
		RequestContext context = RestUtil.getRequestContext(request);
		CrudResource resource = getResource();
		resource.update(uuid, post, context);
		return RestUtil.noContent(response);
	}
	
	/**
	 * @param uuid
	 * @param reason
	 * @param request
	 * @throws Exception
	 */
	@RequestMapping(value = "/{uuid}", method = RequestMethod.DELETE, params = "!purge")
	@ResponseBody
	public Object delete(@PathVariable("uuid") String uuid,
	                     @RequestParam(value = "reason", defaultValue = "web service call") String reason,
	                     WebRequest request, HttpServletResponse response) throws ResponseException {
		RequestContext context = RestUtil.getRequestContext(request);
		getResource().delete(uuid, reason, context);
		return RestUtil.noContent(response);
	}
	
	/**
	 * @param uuid
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/{uuid}", method = RequestMethod.DELETE, params = "purge")
	@ResponseBody
	public Object purge(@PathVariable("uuid") String uuid, WebRequest request, HttpServletResponse response)
	                                                                                                        throws ResponseException {
		RequestContext context = RestUtil.getRequestContext(request);
		getResource().purge(uuid, context);
		return RestUtil.noContent(response);
	}
	
	/**
	 * @param query
	 * @param request
	 * @param response
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(method=RequestMethod.GET, params="q")
	@ResponseBody
	public List<Object> search(@RequestParam("q") String query, WebRequest request, HttpServletResponse response) throws ResponseException {
		Searchable searchable;
		try {
			searchable = (Searchable) getResource();
		} catch (ClassCastException ex) {
			throw new ResourceDoesNotSupportOperationException(getResource().getClass().getSimpleName() + " is not Searchable", null);
		}
		RequestContext context = RestUtil.getRequestContext(request);
		context.setRepresentation(Representation.REF);
		return searchable.search(query, context);
	}	
	
}
