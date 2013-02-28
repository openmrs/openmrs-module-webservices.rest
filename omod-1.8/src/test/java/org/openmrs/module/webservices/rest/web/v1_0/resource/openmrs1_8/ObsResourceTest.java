/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.ObsResource;

public class ObsResourceTest extends BaseDelegatingResourceTest<ObsResource, Obs> {
	
	private enum ObsType {
		CODED, COMPLEX, DATETIME, DRUG, NUMERIC, TEXT
	}
	
	@Override
	public Obs newObject() {
		return Context.getObsService().getObsByUuid(getUuidProperty());
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		assertPropPresent("person");
		assertPropPresent("concept");
		assertPropPresent("value");
		assertPropEquals("obsDatetime", getObject().getObsDatetime());
		assertPropEquals("accessionNumber", getObject().getAccessionNumber());
		assertPropEquals("obsGroup", getObject().getObsGroup());
		assertPropPresent("groupMembers");
		assertPropEquals("comment", getObject().getComment());
		assertPropPresent("location");
		assertPropPresent("order");
		assertPropPresent("encounter");
		assertPropEquals("voided", getObject().getVoided());
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		super.validateFullRepresentation();
		assertPropPresent("person");
		assertPropPresent("concept");
		assertPropPresent("value");
		assertPropEquals("obsDatetime", getObject().getObsDatetime());
		assertPropEquals("accessionNumber", getObject().getAccessionNumber());
		assertPropEquals("obsGroup", getObject().getObsGroup());
		assertPropPresent("groupMembers");
		assertPropEquals("comment", getObject().getComment());
		assertPropPresent("location");
		assertPropPresent("order");
		assertPropPresent("encounter");
		assertPropEquals("voided", getObject().getVoided());
		assertPropPresent("auditInfo");
	}
	
	@Override
	public String getDisplayProperty() {
		return "WEIGHT (KG): 50.0";
	}
	
	@Override
	public String getUuidProperty() {
		return ResourceTestConstants.OBS_UUID;
	}
	
	@Test
	public void asRepresentation_shouldReturnProperlyEncodedValues() throws Exception {
		Obs obs = getObject();
		
		// coded
		Concept concept = Context.getConceptService().getConceptByUuid("a09ab2c5-878e-4905-b25d-5784167d0216");
		clearAndSetValue(obs, ObsType.CODED, concept);
		SimpleObject rep = (SimpleObject) getResource().asRepresentation(getObject(), Representation.DEFAULT);
		Assert.assertTrue(rep.keySet().contains("value"));
		rep = (SimpleObject) rep.get("value");
		Assert.assertEquals("coded", concept.getUuid(), rep.get("uuid"));
		
		// datetime
		Date datetime = new Date();
		clearAndSetValue(obs, ObsType.DATETIME, datetime);
		rep = (SimpleObject) getResource().asRepresentation(getObject(), Representation.DEFAULT);
		Assert.assertEquals("datetime", datetime, ConversionUtil.convert(rep.get("value"), Date.class));
		
		// drug
		Drug drug = Context.getConceptService().getDrugByUuid("3cfcf118-931c-46f7-8ff6-7b876f0d4202");
		clearAndSetValue(obs, ObsType.DRUG, drug);
		rep = (SimpleObject) getResource().asRepresentation(getObject(), Representation.DEFAULT);
		Assert.assertTrue(rep.keySet().contains("value"));
		rep = (SimpleObject) rep.get("value");
		Assert.assertEquals("drug", drug.getUuid(), rep.get("uuid"));
		
		// string-based (complex, text)
		String test = "whoa";
		clearAndSetValue(obs, ObsType.COMPLEX, test);
		rep = (SimpleObject) getResource().asRepresentation(getObject(), Representation.DEFAULT);
		Assert.assertEquals("complex", test, rep.get("value"));
		
		clearAndSetValue(obs, ObsType.TEXT, test);
		rep = (SimpleObject) getResource().asRepresentation(getObject(), Representation.DEFAULT);
		Assert.assertEquals("text", test, rep.get("value"));
		
		// numeric
		Double number = Double.MAX_VALUE;
		clearAndSetValue(obs, ObsType.NUMERIC, number);
		rep = (SimpleObject) getResource().asRepresentation(getObject(), Representation.DEFAULT);
		Assert.assertEquals("numeric", number, rep.get("value"));
	}
	
	private void clearAndSetValue(Obs obs, ObsType type, Object value) {
		obs.setValueCoded(type.equals(ObsType.CODED) ? (Concept) value : null);
		obs.setValueComplex(type.equals(ObsType.COMPLEX) ? (String) value : null);
		obs.setValueDatetime(type.equals(ObsType.DATETIME) ? (Date) value : null);
		obs.setValueDrug(type.equals(ObsType.DRUG) ? (Drug) value : null);
		obs.setValueNumeric(type.equals(ObsType.NUMERIC) ? (Double) value : null);
		obs.setValueText(type.equals(ObsType.TEXT) ? (String) value : null);
	}
}
