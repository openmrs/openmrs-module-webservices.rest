/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_9;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.openmrs.util.OpenmrsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/frontend/config.json")
public class FrontendJsonConfigController1_9 extends BaseRestController {

    private static final String DEFAULT_FRONTEND_DIRECTORY = "frontend";
    private static final String GP_LOCAL_DIRECTORY = "spa.local.directory";
    private static final String JSON_CONFIG_FILE_NAME = "config.json";
    private static final Logger log = LoggerFactory.getLogger(FrontendJsonConfigController1_9.class);

    @RequestMapping(method = RequestMethod.GET)
    public void getFrontendConfigFile(HttpServletRequest request, HttpServletResponse response,
            @RequestParam(value = "download", required = false) String download) throws IOException {
        try {
            File jsonConfigFile = getJsonConfigFile();
            if (!jsonConfigFile.exists()) {
                log.warn("Configuration file does not exist");
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Configuration file does not exist");
            }
            InputStream inputStream = new FileInputStream(jsonConfigFile);
            OutputStream outStream = response.getOutputStream();
            OpenmrsUtil.copyFile(inputStream, outStream);
            boolean isDownloadRequested = Boolean.parseBoolean(download);
            if (isDownloadRequested | download != null && download.isEmpty()) {
                response.setHeader("Content-Disposition", "attachment; filename=" + jsonConfigFile.getName());
            }
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (IOException e) {
            log.error("Error reading Configuration file {}", JSON_CONFIG_FILE_NAME, e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
			    "Error reading Configuration file: " + JSON_CONFIG_FILE_NAME);
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void saveFrontendConfigFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = Context.getAuthenticatedUser();
        if (user == null || !user.isSuperUser()) {
            log.error("Authorization error while saving a config.json file");
			response.sendError(HttpServletResponse.SC_FORBIDDEN,
			    "Authorization error. Admin privileges required to save the " + JSON_CONFIG_FILE_NAME + " file");
            return;
        }
        saveJsonConfigFile(request, response);
    }

    private void saveJsonConfigFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestBody = IOUtils.toString(request.getInputStream() , "UTF-8");

        try {
            // verify that is in a valid JSON format
            new ObjectMapper().readTree(requestBody);
            File jsonConfigFile = getJsonConfigFile();
            OutputStream outStream = Files.newOutputStream(jsonConfigFile.toPath());
            outStream.write(requestBody.getBytes(StandardCharsets.UTF_8));
            log.debug("file: '{}' written successfully", jsonConfigFile.getAbsolutePath());
            response.setStatus(HttpServletResponse.SC_OK);
        }
        catch (JsonProcessingException e) {
            log.error("Invalid JSON format when reading: {}", requestBody, e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON format");
        }
        catch (IOException e) {
            log.error("Error while saving a config.json file: {}", JSON_CONFIG_FILE_NAME, e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error while saving a config.json file");
        }
    }

    private File getJsonConfigFile() throws IOException {
        File folder = getSpaStaticFilesDir();
        if (!folder.isDirectory()) {
            log.debug("Unable to find the OpenMRS SPA module frontend directory hence creating it at: " + folder.getAbsolutePath());
            if (!folder.mkdirs()) {
                log.debug("Failed to create the OpenMRS SPA module frontend directory at: " + folder.getPath());
                throw new IOException("Failed to create the OpenMRS SPA module frontend directory at: " + folder.getPath());
            }
        }
        return new File(folder.getAbsolutePath(), JSON_CONFIG_FILE_NAME);
    }

    private File getSpaStaticFilesDir() {
        String folderName = Context.getAdministrationService().getGlobalProperty(GP_LOCAL_DIRECTORY, DEFAULT_FRONTEND_DIRECTORY);

        // try to load the repository folder straight away.
        File folder = new File(folderName);

        // if the property wasn't a full path already, assume it was intended to be a
        // folder in the application directory
        if (!folder.isAbsolute()) {
            folder = new File(OpenmrsUtil.getApplicationDataDirectory(), folderName);
        }
        return folder;
    }
}
