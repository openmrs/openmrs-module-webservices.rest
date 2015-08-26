package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_9;

import org.apache.commons.io.IOUtils;
import org.openmrs.api.DatatypeService;
import org.openmrs.api.db.ClobDatatypeStorage;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/clobdata")
public class ClobDatatypeStorageController{

    @Autowired
    private DatatypeService datatypeService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public String create(@RequestParam MultipartFile file, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        ClobDatatypeStorage clobData = new ClobDatatypeStorage();
        String encoding = request.getHeader("Content-Encoding");
        clobData.setValue(IOUtils.toString(file.getInputStream(), encoding));
        clobData = datatypeService.saveClobDatatypeStorage(clobData);
        response.setStatus(HttpServletResponse.SC_CREATED);
        return clobData.getUuid();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{uuid}")
    public void retrieve(@PathVariable("uuid") String uuid, HttpServletRequest request, HttpServletResponse response)
        throws Exception{
        ClobDatatypeStorage clobData = datatypeService.getClobDatatypeStorageByUuid(uuid);

        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            writer.print(clobData.getValue());
            writer.flush();
        }finally {
            if(writer != null) {
                writer.close();
            }
        }

    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{uuid}")
    public void delete(@PathVariable("uuid") String uuid, HttpServletRequest request, HttpServletResponse response) {
        ClobDatatypeStorage clobData = datatypeService.getClobDatatypeStorageByUuid(uuid);
        if(clobData != null) {
            datatypeService.deleteClobDatatypeStorage(clobData);
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        }
    }
}

