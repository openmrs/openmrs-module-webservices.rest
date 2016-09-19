package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9;


import org.openmrs.Relationship;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.RelationshipResource1_8;

/**
 * {@link org.openmrs.module.webservices.rest.web.annotation.Resource} for Provider, supporting standard CRUD operations
 */
@Resource(name = RestConstants.VERSION_1 + "/relationship", supportedClass = Relationship.class, supportedOpenmrsVersions = {"1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*", "2.1.*"})
public class RelationshipResource1_9 extends RelationshipResource1_8{
    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
     */
    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        DelegatingResourceDescription description = super.getRepresentationDescription(rep);
        if (rep instanceof DefaultRepresentation) {
            description.addProperty("startDate");
            description.addProperty("endDate");
            return description;
        } else if(rep instanceof FullRepresentation){
            description.addProperty("startDate");
            description.addProperty("endDate");
            return description;
        }
        return null;
    }

    @Override
    public String getResourceVersion(){
        return RestConstants1_9.RESOURCE_VERSION;
    }
}
