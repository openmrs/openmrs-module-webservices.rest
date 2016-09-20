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
package org.openmrs.module.webservices.rest.web.v1_0.controller;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping;
import org.xml.sax.InputSource;

/**
 * Facilitates testing controllers.
 */
public abstract class MainResourceControllerTest extends BaseModuleWebContextSensitiveTest {
	
	@Autowired
	private AnnotationMethodHandlerAdapter handlerAdapter;
	
	@Autowired
	private List<DefaultAnnotationHandlerMapping> handlerMappings;
	
	/**
	 * Creates a request from the given parameters.
	 * <p>
	 * The requestURI is automatically preceded with "/rest/" + RestConstants.VERSION_1.
	 * 
	 * @param method
	 * @param requestURI
	 * @return
	 */
	public MockHttpServletRequest request(RequestMethod method, String requestURI) {
		MockHttpServletRequest request = new MockHttpServletRequest(method.toString(), "/rest/" + getNamespace() + "/"
		        + requestURI);
		request.addHeader("content-type", "application/json");
		return request;
	}
	
	/**
	 * Override this method to test a different namespace than v1.
	 * 
	 * @return the namespace
	 */
	public String getNamespace() {
		return RestConstants.VERSION_1;
	}
	
	public static class Parameter {
		
		public String name;
		
		public String value;
		
		public Parameter(String name, String value) {
			this.name = name;
			this.value = value;
		}
	}
	
	public MockHttpServletRequest newRequest(RequestMethod method, String requestURI, Parameter... parameters) {
		MockHttpServletRequest request = request(method, requestURI);
		for (Parameter parameter : parameters) {
			request.addParameter(parameter.name, parameter.value);
		}
		return request;
	}
	
	public MockHttpServletRequest newDeleteRequest(String requestURI, Parameter... parameters) {
		return newRequest(RequestMethod.DELETE, requestURI, parameters);
	}
	
	public MockHttpServletRequest newGetRequest(String requestURI, Parameter... parameters) {
		return newRequest(RequestMethod.GET, requestURI, parameters);
	}
	
	public MockHttpServletRequest newPostRequest(String requestURI, Object content) {
		MockHttpServletRequest request = request(RequestMethod.POST, requestURI);
		try {
			String json = new ObjectMapper().writeValueAsString(content);
			request.setContent(json.getBytes("UTF-8"));
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		return request;
	}
	
	public MockHttpServletRequest newPostRequest(String requestURI, String content) {
		MockHttpServletRequest request = request(RequestMethod.POST, requestURI);
		try {
			request.setContent(content.getBytes("UTF-8"));
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		return request;
	}
	
	public MockHttpServletRequest newPutRequest(String requestURI, Object content) {
		try {
			String json = new ObjectMapper().writeValueAsString(content);
			return newPutRequest(requestURI, json);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public MockHttpServletRequest newPutRequest(String requestURI, String content) {
		MockHttpServletRequest request = request(RequestMethod.PUT, requestURI);
		try {
			request.setContent(content.getBytes("UTF-8"));
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		return request;
	}
	
	/**
	 * Passes the given request to a proper controller.
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public MockHttpServletResponse handle(HttpServletRequest request) throws Exception {
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		HandlerExecutionChain handlerExecutionChain = null;
		for (DefaultAnnotationHandlerMapping handlerMapping : handlerMappings) {
			handlerExecutionChain = handlerMapping.getHandler(request);
			if (handlerExecutionChain != null) {
				break;
			}
		}
		Assert.assertNotNull("The request URI does not exist", handlerExecutionChain);
		
		handlerAdapter.handle(request, response, handlerExecutionChain.getHandler());
		
		return response;
	}
	
	/**
	 * Deserializes the JSON response.
	 * 
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public SimpleObject deserialize(MockHttpServletResponse response) throws Exception {
		return new ObjectMapper().readValue(response.getContentAsString(), SimpleObject.class);
	}
	
	@Test
	public void shouldGetDefaultByUuid() throws Exception {
		MockHttpServletResponse response = handle(request(RequestMethod.GET, getURI() + "/" + getUuid()));
		SimpleObject result = deserialize(response);
		
		Assert.assertNotNull(result);
		Assert.assertEquals(getUuid(), PropertyUtils.getProperty(result, "uuid"));
	}
	
	@Test
	public void shouldGetRefByUuid() throws Exception {
		MockHttpServletRequest request = request(RequestMethod.GET, getURI() + "/" + getUuid());
		request.addParameter("v", "ref");
		SimpleObject result = deserialize(handle(request));
		
		Assert.assertNotNull(result);
		Assert.assertEquals(getUuid(), PropertyUtils.getProperty(result, "uuid"));
	}
	
	@Test
	public void shouldGetFullByUuid() throws Exception {
		MockHttpServletRequest request = request(RequestMethod.GET, getURI() + "/" + getUuid());
		request.addParameter("v", "full");
		SimpleObject result = deserialize(handle(request));
		
		Assert.assertNotNull(result);
		Assert.assertEquals(getUuid(), PropertyUtils.getProperty(result, "uuid"));
	}
	
	@Test
	public void shouldGetAll() throws Exception {
		SimpleObject result = deserialize(handle(request(RequestMethod.GET, getURI())));
		
		Assert.assertNotNull(result);
		Assert.assertEquals(getAllCount(), Util.getResultsSize(result));
	}
	
	/**
	 * @return the URI of the resource
	 */
	public abstract String getURI();
	
	/**
	 * @return the uuid of an existing object
	 */
	public abstract String getUuid();
	
	/**
	 * @return the count of all not retired/voided objects
	 */
	public abstract long getAllCount();
	
	/**
	 * Evaluates an XPath expression on a XML string
	 * 
	 * @param xml
	 * @param xPath
	 * @return
	 * @throws XPathExpressionException
	 */
	protected String evaluateXPath(String xml, String xPath) throws XPathExpressionException {
		InputSource source = new InputSource(new StringReader(xml));
		XPath xpath = XPathFactory.newInstance().newXPath();
		return xpath.evaluate(xPath, source);
	}
	
	/**
	 * Prints an XML string indented
	 * 
	 * @param xml
	 * @throws TransformerException
	 */
	protected void printXML(String xml) throws TransformerException {
		
		Source xmlInput = new StreamSource(new StringReader(xml));
		StringWriter stringWriter = new StringWriter();
		
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.transform(xmlInput, new StreamResult(stringWriter));
		
		System.out.println(stringWriter.toString());
	}
	
	public String getBaseRestURI() {
		return "/rest/" + getNamespace() + "/";
	}
}
