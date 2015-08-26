package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_9;

import org.openmrs.FormResource;
import org.openmrs.api.FormService;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceController;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9.FormResourceResource1_9;
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

@Controller("webservices.rest.formResourceController")
@RequestMapping(value="/rest/" + RestConstants.VERSION_1 + "/form/{uuid}/resource/{resourceUuid}/value")
public class FormResourceController1_9 extends MainResourceController {
    @Autowired
    private ClobDatatypeStorageController clobDatatypeStorageController;

    @Autowired
    private FormService formService;

    @RequestMapping(value="/rest/" + RestConstants.VERSION_1 + "/form/{uuid}/resource/{resourceUuid}/value",
                    method = RequestMethod.POST, headers = {"Content-Type=multipart/form-data"})
    @ResponseBody
    public Object createResourceValue(@PathVariable("uuid") String formUuid,
                                      @PathVariable("resourceUuid") String resourceUuid,
                                      @RequestParam("value") MultipartFile file,
                                      HttpServletRequest request, HttpServletResponse response) throws Exception{

        //Get the resource
        FormResource resource = formService.getFormResourceByUuid(resourceUuid);
        if(resource == null) {
            throw new IllegalArgumentException("No form resource with uuid " + resourceUuid + " found");
        }

        String clobUuid = clobDatatypeStorageController.create(file, request, response);

        resource.setValueReferenceInternal(clobUuid);
        formService.saveFormResource(resource);

        return new FormResourceResource1_9().asDefaultRep(resource);
    }

    @RequestMapping(value="/rest/" + RestConstants.VERSION_1 + "/form/{uuid}/resource/{resourceUuid}/value",
            method = RequestMethod.GET)
    public void getResourceValue(@PathVariable("uuid") String formUuid,
                                      @PathVariable("resourceUuid") String resourceUuid,
                                      HttpServletRequest request, HttpServletResponse response) throws Exception{
        //Get the resource
        FormResource resource = formService.getFormResourceByUuid(resourceUuid);
        if(resource == null) {
            throw new IllegalArgumentException("No form resource with uuid " + resourceUuid + " found");
        }
        clobDatatypeStorageController.retrieve(resource.getValueReference(), request, response);

        response.setHeader("Content-Disposition", "attachment;filename=\""+resource.getName()+"\"");
    }
}
