package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8;

import org.openmrs.api.context.Context;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingReadableResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 */
@Resource(name = RestConstants.VERSION_1 + "/module", supportedClass = Module.class, supportedOpenmrsVersions = {"1.8.*", "1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*", "2.1.*"})
public class ModuleResource1_8 extends BaseDelegatingReadableResource<Module> {

    @Override
    public Module getByUniqueId(String uniqueId) {
        for (Module module:ModuleFactory.getLoadedModules()) {
            if(uniqueId.equals(getUuid(module))){
                return module;
            }
        }
        return null;
    }

    @Override
    public Module newDelegate() {
        return null;
    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        if (rep instanceof FullRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("display");
            description.addProperty("name");
            description.addProperty("description");
            description.addProperty("packageName");
            description.addProperty("author");
            description.addProperty("version");
            description.addLink("ref", ".?v=" + RestConstants.REPRESENTATION_REF);
            description.addSelfLink();
            return description;
        } else if (rep instanceof DefaultRepresentation){
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("display");
            description.addProperty("name");
            description.addProperty("description");
            description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
            description.addLink("ref", ".?v=" + RestConstants.REPRESENTATION_REF);
            description.addSelfLink();
            return description;
        } else if (rep instanceof RefRepresentation){
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("display");
            description.addSelfLink();
            return description;
        }
        return null;
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doGetAll(org.openmrs.module.webservices.rest.web.RequestContext)
     */
    @Override
    public NeedsPaging<Module> doGetAll(RequestContext context) throws ResponseException {
        return new NeedsPaging<Module>(new ArrayList<Module>(ModuleFactory.getLoadedModules()), context);
    }

    @PropertyGetter("uuid")
    public static String getUuid(Module instance){
        return UUID.nameUUIDFromBytes(instance.getName().getBytes()).toString();
    }

    @PropertyGetter("display")
    public static String getDisplay(Module instance){
        return instance.getName();
    }


}
