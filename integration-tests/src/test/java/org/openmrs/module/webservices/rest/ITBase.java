/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest;

import static io.restassured.RestAssured.basic;

import io.restassured.RestAssured;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.BeforeClass;

public abstract class ITBase {
	
	private static final Object serverStartupLock = new Object();
	
	private static boolean serverStarted = false;
	
	public static final String ADMIN_USERNAME;
	
	public static final String ADMIN_PASSWORD;
	
	public static final URI TEST_URL;
	
	static {
		String testUrlProperty = System.getProperty("testUrl", "http://admin:Admin123@localhost:8080/openmrs");
		try {
			TEST_URL = new URI(testUrlProperty);
			RestAssured.baseURI = TEST_URL.getScheme() + "://" + TEST_URL.getHost();
			RestAssured.port = TEST_URL.getPort();
			RestAssured.basePath = TEST_URL.getPath() + "/ws/rest/v1";
			String[] userInfo = TEST_URL.getUserInfo().split(":");
			ADMIN_USERNAME = userInfo[0];
			ADMIN_PASSWORD = userInfo[1];
			RestAssured.authentication = basic(userInfo[0], userInfo[1]);
		}
		catch (URISyntaxException e) {
			throw new RuntimeException("Invalid uri: " + testUrlProperty, e);
		}
	}
	
	@BeforeClass
	public static void waitForServerToStart() {
		synchronized (serverStartupLock) {
			if (!serverStarted) {
				final long TIME = System.currentTimeMillis();
				final int TIME_OUT = 300000;
				final int RETRY_AFTER = 10000;
				
				final RequestConfig REQUEST_CONFIG = RequestConfig.custom().setSocketTimeout(RETRY_AFTER)
				        .setConnectTimeout(RETRY_AFTER).build();
				
				final String STARTUP_URI = TEST_URL.getScheme() + "://" + TEST_URL.getHost() + ":"
				        + TEST_URL.getPort() + TEST_URL.getPath();
				System.out.println("Waiting for server at " + STARTUP_URI + " for " + TIME_OUT / 1000 + " more seconds...");
				
				while (System.currentTimeMillis() - TIME < TIME_OUT) {
					try {
						final HttpClient CLIENT = HttpClientBuilder.create().disableAutomaticRetries().build();
						final HttpGet SESSION_GET = new HttpGet(STARTUP_URI);
						SESSION_GET.setConfig(REQUEST_CONFIG);
						final HttpClientContext CONTEXT = HttpClientContext.create();
						final HttpResponse RESPONSE = CLIENT.execute(SESSION_GET, CONTEXT);
						
						int status = RESPONSE.getStatusLine().getStatusCode();
						if (status >= 400) {
							throw new RuntimeException(status + " " + RESPONSE.getStatusLine().getReasonPhrase());
						}
						
						URI finalUri = SESSION_GET.getURI();
						List<URI> redirectLocations = CONTEXT.getRedirectLocations();
						if (redirectLocations != null) {
							finalUri = redirectLocations.get(redirectLocations.size() - 1);
						}
						
						String finalUriString = finalUri.toString();
						if (!finalUriString.contains("initialsetup")) {
							serverStarted = true;
							return;
						}
					}
					catch (IOException e) {
						System.out.println(e.toString());
					}
					
					try {
						System.out.println("Waiting for " + (TIME_OUT - (System.currentTimeMillis() - TIME)) / 1000 + " more seconds...");
						Thread.sleep(RETRY_AFTER);
					}
					catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}
				throw new RuntimeException("Server startup took longer than 5 minutes!");
			}
		}
	}
}
