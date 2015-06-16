package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9;

import org.openmrs.EncounterProvider;
import org.openmrs.EncounterRole;
import org.openmrs.Provider;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_9;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class EncounterProviderResource1_9Test extends
        BaseDelegatingResourceTest<EncounterProviderResource1_9,EncounterProvider> {

    @Override
    public void validateDefaultRepresentation() throws Exception {
        assertPropPresent("provider");
        assertPropPresent("encounterRole");
        assertPropEquals("encounterRole",getObject().getEncounterRole());
        assertPropEquals("provider",getObject().getProvider());
        assertPropEquals("uuid",getObject().getUuid());
    }

    @Override
    public EncounterProvider newObject() {
        EncounterProvider encounterProvider = new EncounterProvider();
        encounterProvider.setId(1);
        encounterProvider.setUuid(getUuidProperty());
        Provider provider = new Provider(1000);
        provider.setName("Testing Provider");

        EncounterRole rolePlayed = new EncounterRole(3000);
        rolePlayed.setName("Nurse");
        return encounterProvider;
    }

    @Override
    public String getDisplayProperty() {
        EncounterProvider encounterProvider = newObject();
        Provider provider = encounterProvider.getProvider();
        EncounterRole role = encounterProvider.getEncounterRole();

        if(role == null) {
            if(provider == null) {
                return null;
            }
            return provider.getName();
        }

        return provider.getName() + ": " + role.getName();
    }

    @Override
    public String getUuidProperty() {
        return RestTestConstants1_9.ENCOUNTER_PROVIDER_UUID;
    }
}
