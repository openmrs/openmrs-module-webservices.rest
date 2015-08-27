package org.openmrs.module.webservices.rest.web.v1_0.controller;

import org.openmrs.api.DatatypeService;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.ClobDatatypeStorage;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/clobdata")
public class ClobDatatypeStorageController {

    @Autowired
    private RestService restService;

    @RequestMapping(method = RequestMethod.POST, headers = {"Content-Type=multipart/form-data"})
    @ResponseBody
    public Object create(@RequestBody MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        DatatypeService ds = Context.getService(DatatypeService.class);
        ClobDatatypeStorage resource = new ClobDatatypeStorage();

        return null;
    }

}
