package org.openmrs.module.webservices.rest.web.resource.impl;

import org.apache.commons.beanutils.PropertyUtils;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.util.ReflectionUtil;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.Hyperlink;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.Converter;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import org.mockito.Matchers;

public class ImplementedBaseDelegatingConverter {
	
	@Test(expected = NullPointerException.class)
	public void asRepresentationNullPointerTest() {
		BaseDelegatingConverter mockedConverter = mock(BaseDelegatingConverter.class);
		when(mockedConverter.asRepresentation(anyObject(), any(Representation.class))).thenCallRealMethod();
		mockedConverter.asRepresentation(null, (Representation) null);
	}
	
	//@Test(expected=NullPointerException.class)
	@Test
	public void simpleRepTest() {
		
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("allergenType", Representation.DEFAULT);
		description.addProperty("codedAllergen", Representation.DEFAULT);
		description.addProperty("nonCodedAllergen");
		
		String del = "delegateString";
		BaseDelegatingConverter mockedConverter = mock(BaseDelegatingConverter.class);
		when(mockedConverter.asRepresentation(anyObject(), any(Representation.class))).thenCallRealMethod();
		when(mockedConverter.getRepresentationDescription(any(Representation.class))).thenReturn(description);
		mockedConverter.asRepresentation(del, (Representation) null);
		
	}
	
	/*
	@Test
	public void simpleRepTest(){

		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("allergenType", Representation.DEFAULT);
		description.addProperty("codedAllergen", Representation.DEFAULT);
		description.addProperty("nonCodedAllergen");

		BaseDelegatingConverter mockedConverter= mock(BaseDelegatingConverter.class);
		when(mockedConverter.asRepresentation(anyObject(), any(Representation.class))).thenCallRealMethod();
		when(mockedConverter.getRepresentationDescription( any(Representation.class))).thenReturn(description);
		mockedConverter.asRepresentation(null, (Representation)null);		


	}*/
	
}
