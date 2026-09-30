/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.resource.api;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.IllegalRequestException;
import org.openmrs.module.webservices.rest.web.response.InvalidSearchException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * A SearchHandler that binds request parameters into a typed params object.
 * <p>
 * Subclasses implement {@link #search(Object)} returning the matching delegates; the base class binds
 * the request parameters into the typed params object {@code P}.
 *
 * @param <D> the delegate type returned by the search (ex: Encounter)
 * @param <R> the resource class this handler searches (ex: EncounterResource2_2)
 * @param <P> the params type (ex: EncounterSearchHandler2_0.Params)
 */
public abstract class TypedSearchHandler<D, R extends DelegatingCrudResource<D>, P> implements SearchHandler {

	private TypedSearchConfig<R, P> searchConfig;

	protected TypedSearchHandler(TypedSearchConfig<R, P> searchConfig) {
		this.searchConfig = searchConfig;
	}
	/**
	 * @return the search configuration
	 */
	@Override
	public final TypedSearchConfig<R, P> getSearchConfig() {
		return searchConfig;
	}

	@Override
	public final PageableResult search(RequestContext context) throws ResponseException {
		Class<P> paramsClass = searchConfig.getParamsClass();
		HttpServletRequest request = context.getRequest();
		ObjectMapper objectMapper = createObjectMapper();

		// read from query params
		try {
			P params = objectMapper.convertValue(request.getParameterMap(), paramsClass);
			assertRequiredParamsPresent(params);
			return page(search(params), context);
		} catch(IllegalArgumentException e) {
			throw new IllegalRequestException("Unable to read search request parameters", e);
		}
	}

	/**
	 * Creates an ObjectMapper configured with a custom String deserializer that
	 * handles array-to-string coercion (for query parameter maps where values
	 * are String arrays).
	 */
	private ObjectMapper createObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		// Ignore query parameters that aren't fields on the params class - notably the framework's
		// special request parameters (limit, startIndex, v, s, ...) which ride along on every request
		// and are handled elsewhere. Missing required params are still caught by
		// assertRequiredParamsPresent(); only unrecognized extras are silently dropped.
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		SimpleModule module = new SimpleModule();
		module.addDeserializer(String.class, new JsonDeserializer<String>() {
			@Override
			public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
				if (p.isExpectedStartArrayToken()) {
					String[] array = p.readValueAs(String[].class);
					return (array != null && array.length > 0) ? array[0] : null;
				} else {
					return p.getText();
				}
			}
		});
		objectMapper.registerModule(module);
		return objectMapper;
	}

	private void assertRequiredParamsPresent(P params) throws ResponseException {
		try {
			Class<P> paramsClass = searchConfig.getParamsClass();
			for(Field f : paramsClass.getDeclaredFields()) {
				boolean isOptional = f.getAnnotation(OptionalParameter.class) != null;
				if(!isOptional) {
					f.setAccessible(true);
					if(f.get(params) == null) {
						throw new InvalidSearchException("Missing required search parameter: " + f.getName());
					}
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new IllegalRequestException("Failed to perform reflection to check required parameters", e);
		}
	}

	private PageableResult page(List<D> results, RequestContext context) {
		if(results.isEmpty()) {
			return new EmptySearchResult();
		} else {
			return new NeedsPaging<D>(results, context);
		}
	}

	public abstract List<D> search(P params) throws ResponseException;
}
