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
package org.openmrs.module.webservices.rest.doc;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.docs.swagger.SwaggerSpecificationCreator;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SwaggerSpecificationCreatorTest extends BaseModuleWebContextSensitiveTest {
	
	Map<String, Integer> beforeCounts;
	
	public Map<String, Integer> getRowCounts() throws Exception {
		Map<String, Integer> ret = new HashMap<String, Integer>();
		
		Connection con = this.getConnection();
		DatabaseMetaData metaData = con.getMetaData();
		
		ResultSet rs = metaData.getTables(null, "PUBLIC", "%", null);
		while (rs.next()) {
			String tableName = rs.getString(3);
			
			Statement stmt = con.createStatement();
			
			ResultSet resultSet = stmt.executeQuery("SELECT count(*) AS rowcount FROM " + tableName);
			
			resultSet.next();
			
			ret.put(tableName, resultSet.getInt("rowcount"));
			
			resultSet.close();
		}
		
		return ret;
	}
	
	private void showTableContents(String tableName) throws Exception {
		Connection con = this.getConnection();
		Statement stmt = con.createStatement();
		ResultSet resultSet = stmt.executeQuery("SELECT * FROM " + tableName);
		
		ResultSetMetaData metaData = resultSet.getMetaData();
		int columnCount = metaData.getColumnCount();
		
		while (resultSet.next()) {
			for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
				Object object = resultSet.getObject(columnIndex);
				System.out.printf("%s, ", object == null ? "NULL" : object.toString());
			}
			System.out.printf("%n");
		}
	}
	
	@Before
	public void init() throws Exception {
		
		// init REST
		Context.getService(RestService.class).initialize();
		
		Context.getAdministrationService().saveGlobalProperty(
		    new GlobalProperty(RestConstants.SWAGGER_QUIET_DOCS_GLOBAL_PROPERTY_NAME, "true"));
		
		// ensure GP is written to database before we count the rows
		Context.flushSession();
		
		beforeCounts = getRowCounts();
	}
	
	@Test
	public void checkNoDatabaseChanges() throws Exception {
		
		SwaggerSpecificationCreator ssc = new SwaggerSpecificationCreator("/v1/");
		ssc.BuildJSON();
		
		Map<String, Integer> afterCounts = getRowCounts();
		
		Assert.assertTrue(ensureCountsEqual(beforeCounts, afterCounts));
	}
	
	private boolean ensureCountsEqual(Map<String, Integer> beforeCounts, Map<String, Integer> afterCounts) throws Exception {
		if (beforeCounts.size() != afterCounts.size()) {
			System.err.println("There are a different number of tables.");
			return false;
		}
		
		for (String key : beforeCounts.keySet()) {
			if (beforeCounts.get(key) != afterCounts.get(key)) {
				System.err.println("The " + key + " table has a different number of rows (" + beforeCounts.get(key)
				        + " before, " + afterCounts.get(key) + " after).");
				
				showTableContents(key);
				
				return false;
			}
		}
		
		return true;
	}
}
