package org.openmrs.module.webservices.rest.web.resource.impl;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.Hyperlink;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

import java.lang.reflect.ParameterizedType;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.*;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import org.mockito.Matchers;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.openmrs.OpenmrsMetadata;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;

import org.openmrs.api.context.ServiceContext;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.RepHandler;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import org.springframework.context.ApplicationContext;

import org.openmrs.module.webservices.rest.web.HivDrugOrderSubclassHandler;

public class ImplementedBaseDelegatingResourceTest extends BaseDelegatingResource<Integer> {
	
	public void purge(Integer delegate, RequestContext context) throws ResponseException {
	}
	
	public Integer getByUniqueId(String uniqueId) {
		
		return 0;
	}
	
	protected void delete(Integer delegate, String reason, RequestContext context) throws ResponseException {
	}
	
	public Integer save(Integer s) {
		
		return 0;
		
	}
	
	public Integer newDelegate() {
		
		return 0;
		
	}
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("name");
			description.addProperty("description");
			description.addProperty("retired");
			description.addSelfLink();
			if (rep instanceof DefaultRepresentation) {
				description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			} else {
				description.addProperty("auditInfo");
			}
			return description;
		}
		return null;
	}
	
	@Test
	public void doesNotHaveTypeDefined() {
		Assert.assertFalse(hasTypesDefined());
	}
	
	@Test
	public void initTest() {
		
		//setup the contexts
		ServiceContext scon = ServiceContext.getInstance();
		ApplicationContext mockContext = mock(ApplicationContext.class);
		
		HivDrugOrderSubclassHandler thismightwork = new HivDrugOrderSubclassHandler();
		Map<String, DelegatingSubclassHandler> mockedMap = new HashMap<String, DelegatingSubclassHandler>();
		mockedMap.put("foo", thismightwork);
		
		when(mockContext.getBeansOfType(DelegatingSubclassHandler.class)).thenReturn(mockedMap);
		
		scon.setApplicationContext(mockContext);
		
		init();
		
	}
	
}
