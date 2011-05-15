package org.openmrs.module.webservices.rest.web.deletebeforerelease;

import org.apache.commons.beanutils.PropertyUtils;
import org.openmrs.PatientIdentifierType;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.Converter;
import org.openmrs.module.webservices.rest.web.response.ConversionException;

/**
 * Temporary, for testing creating a patient. Delete this when we implement the
 * PatientIdentifierTypeResource
 */
@Handler(supports = PatientIdentifierType.class, order = 0)
public class PatientIdentifierTypeConverter implements Converter<PatientIdentifierType> {
	
	@Override
	public PatientIdentifierType getByUniqueId(String uuidOrName) {
		PatientIdentifierType ret = Context.getPatientService().getPatientIdentifierTypeByUuid(uuidOrName);
		if (ret == null)
			ret = Context.getPatientService().getPatientIdentifierTypeByName(uuidOrName);
		return ret;
	}
	
	@Override
	public Object asRepresentation(PatientIdentifierType instance, Representation rep) throws ConversionException {
		throw new RuntimeException("Not implemented");
	}
	
	@Override
	public Object getProperty(PatientIdentifierType instance, String propertyName) throws ConversionException {
		try {
			return PropertyUtils.getProperty(instance, propertyName);
		}
		catch (Exception ex) {
			throw new ConversionException(null, ex);
		}
	}
	
	@Override
	public void setProperty(PatientIdentifierType instance, String propertyName, Object value) throws ConversionException {
		try {
			PropertyUtils.setProperty(instance, propertyName, value);
		}
		catch (Exception ex) {
			throw new ConversionException(null, ex);
		}
	}
	
}
