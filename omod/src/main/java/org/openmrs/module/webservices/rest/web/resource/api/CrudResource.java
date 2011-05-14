package org.openmrs.module.webservices.rest.web.resource.api;

/**
 * Indicates that the resource implements all standard OpenMRS CRUD operations: create, retrieve,
 * update, delete (void/retire), purge
 */
public interface CrudResource extends Resource, Creatable, Retrievable, Updatable, Deletable, Purgeable {

}
