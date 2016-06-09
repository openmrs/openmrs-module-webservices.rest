package org.openmrs.module.webservices.rest.web.resource.impl;

import org.junit.Assert;
import org.junit.Test;
import java.lang.reflect.Method;
import java.util.List;
import java.util.LinkedList;
import org.openmrs.api.OpenmrsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ServiceContext;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.resource.api.Searchable;
import java.lang.reflect.ParameterizedType;

import org.openmrs.api.AdministrationService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;
import org.mockito.Matchers;

public class ServiceSearcherTest {
	
	@Test
	public void ServiceSearcherShouldReturnSearch() {
		ServiceContext scon = ServiceContext.getInstance();
		PatientService pat = mock(PatientService.class);
		AdministrationService adm = mock(AdministrationService.class);
		LinkedList retList = new LinkedList();
		retList.add(1);
		when(pat.getPatients(anyString(), anyInt(), anyInt())).thenReturn(retList);
		scon.setPatientService(pat);
		scon.setAdministrationService(adm);
		
		RequestContext rcon = new RequestContext();
		rcon.setStartIndex(0);
		rcon.setLimit(1);
		
		ServiceSearcher<String> S = new ServiceSearcher(PatientService.class, "getPatients", "getCountOfPatients");
		
		AlreadyPaged res = S.search("foo", rcon);
		Assert.assertEquals(retList, res.getPageOfResults());
	}
}
