package org.openmrs.module.webservices.rest.resource.api;

/**
 * Indicates that the resource implements all standard OpenMRS CRUD operations: create, retrieve,
 * update, delete (void/retire), purge
 */
public interface CrudResource extends Creatable, Retrievable, Updatable, Deletable, Purgeable {

}
