package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_0;

import java.lang.reflect.Method;

import org.openmrs.PersonName;
import org.openmrs.layout.name.NameSupport;
import org.openmrs.layout.name.NameTemplate;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_11.PersonResource1_11;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.PersonNameResource1_8;

@SubResource(parent = PersonResource1_11.class, path = "name", supportedClass = PersonName.class, supportedOpenmrsVersions = {
        "2.0.*" })
public class PersonNameResource2_0 extends PersonNameResource1_8 {

	@Override
	public String getDisplayString(PersonName personName) {
		try {
            NameTemplate nameTemplate = NameSupport.getInstance().getDefaultLayoutTemplate();

            if (nameTemplate!= null) {
                // need to use reflection since the format method was not added until later versions of openmrs
                Method format = NameTemplate.class.getDeclaredMethod("format", PersonName.class);
                return (String) format.invoke(nameTemplate, personName);
            }
        }
        catch (Exception e) {
            // fall through to just returning full name if no format method found or format fails
        }

        // otherwise, just return full name
        return personName.getFullName();
	}
}
