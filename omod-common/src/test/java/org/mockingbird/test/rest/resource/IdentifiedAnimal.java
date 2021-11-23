/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.mockingbird.test.rest.resource;

import org.mockingbird.test.Animal;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * Fake used in tests at
 * {@link org.openmrs.module.webservices.rest.web.api.impl.RestServiceImplTest}. Located in a fake
 * package not under org.openmrs.xxx on purpose otherwise it will be picked up by other tests due to
 * {@link org.openmrs.module.webservices.rest.web.OpenmrsClassScanner} and its classpath pattern.
 * Necessary because existing fake test classes did not have the uuid needed to test
 * fetching resources using the uuid.
 */
public class IdentifiedAnimal extends DelegatingCrudResource<Animal> {
	private String name;
	private String uuid;

	/**
	 * Instantiates a new Animal resource.
	 * @param name the name
	 * @param uuid the uuid
	 */
	public IdentifiedAnimal(String name, String uuid) {
		this.name = name;
		this.uuid = uuid;
	}

	/**
	 * Gets name.
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets uuid.
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * Sets the name
	 * @param name name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the uuid
	 * @param uuid uuid
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * Gets the {@link DelegatingResourceDescription} for the given representation for this
	 * resource, if it exists
	 * @param rep representation
	 * @return representation description
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		return null;
	}

	/**
	 * Gets the delegate object with the given unique id. Implementations may decide whether
	 * "unique id" means the uuid, or if they also want to retrieve delegates based on a unique
	 * human-readable property.
	 * @param uniqueId unique Id
	 * @return the delegate for the given uniqueId
	 */
	@Override
	public Animal getByUniqueId(String uniqueId) {
		return null;
	}

	/**
	 * Void or retire delegate, whichever action is appropriate for the resource type. Subclasses
	 * need to override this method, which is called internally by
	 * {@link #delete(String, String, RequestContext)}.
	 * @param delegate animal object
	 * @param reason reason
	 * @param context context
	 * @throws ResponseException exception
	 */
	@Override
	protected void delete(Animal delegate, String reason, RequestContext context) throws ResponseException {}

	/**
	 * Purge delegate from persistent storage. Subclasses need to override this method, which is
	 * called internally by {@link #purge(String, RequestContext)}.
	 * @param delegate animal object
	 * @param context context
	 * @throws ResponseException exception
	 */
	@Override
	public void purge(Animal delegate, RequestContext context) throws ResponseException {}

	/**
	 * Instantiates a new instance of the handled delegate
	 * @return animal object
	 */
	@Override
	public Animal newDelegate() {
		return null;
	}

	/**
	 * Writes the delegate to the database
	 *
	 * @param delegate animal object
	 * @return the saved instance
	 */
	@Override
	public Animal save(Animal delegate) {
		return null;
	}
}
