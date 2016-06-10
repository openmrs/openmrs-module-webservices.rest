package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8;

import org.openmrs.module.Module;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;


/**
 *
 */
public class ModuleResource1_8Test extends BaseDelegatingResourceTest<ModuleResource1_8, Module> {

    @Override
    public Module newObject() {
        return new Module("Atlas Module", getUuidProperty(), "name", "author", "description", "version");
    }

    @Override
    public String getDisplayProperty() {
        return "Atlas Module";
    }

    @Override
    public String getUuidProperty() {
        return RestTestConstants1_8.MODULE_UUID;
    }
}
