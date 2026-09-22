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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A SearchConfig for TypedSearchHandlers. 
 * @see TypedSearchHandler
 * @see SearchConfig
 * @param <R> The Resource class that this SearchConfig supports
 * @param <P> The SearchParameters class that this SearchConfig supports
 */
public class TypedSearchConfig<R, P> extends SearchConfig {

  private Class<R> resourceClass;
  private Class<P> paramsClass;

  public TypedSearchConfig(String id, Class<R> resourceClass, Class<P> paramsClass, String description) {
    super(
      id, 
      getSupportedClass(resourceClass), 
      getSupportedOpenmrsVersion(resourceClass),
      toSearchQuery(paramsClass, description));

    this.resourceClass = resourceClass;
    this.paramsClass = paramsClass;
  }

  private static String getSupportedClass(Class<?> delegateClass) {
    return delegateClass.getAnnotation(Resource.class).name();
  }

  private static List<String> getSupportedOpenmrsVersion(Class<?> delegateClass) {
    return Arrays.asList(delegateClass.getAnnotation(Resource.class).supportedOpenmrsVersions());
  }

  private static SearchQuery toSearchQuery(Class<?> paramsType, String description) {
    List<String> requiredParams = new ArrayList<>();
    List<String> optionalParams = new ArrayList<>();
    for(Field f : paramsType.getDeclaredFields()) {
      // Skip fields that aren't real declared parameters, e.g. the coverage-tracking field
      // injected by JaCoCo's instrumentation agent (which runs as part of the Maven build) -
      // such fields are synthetic and/or static, unlike any real per-request search parameter.
      if (f.isSynthetic() || Modifier.isStatic(f.getModifiers())) {
        continue;
      }
      boolean isOptional = f.getAnnotation(OptionalParameter.class) != null;
      if(isOptional) {
        optionalParams.add(f.getName());
      } else {
        requiredParams.add(f.getName());
      }
    }

    return new SearchQuery.Builder(description)
      .withRequiredParameters(requiredParams.toArray(new String[requiredParams.size()]))
      .withOptionalParameters(optionalParams.toArray(new String[optionalParams.size()]))
      .build();
  }

  /**
	 * Get this {@code searchQueries}.
	 * 
	 * @return this search queries
	 */
	public SearchQuery getSearchQuery() {
		return getSearchQueries().iterator().next();
	}

  public Class<R> getResourceClass() {
    return resourceClass;
  }

  public Class<P> getParamsClass() {
    return paramsClass;
  }
}
