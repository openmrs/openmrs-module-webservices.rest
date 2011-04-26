package org.openmrs.module.webservices.rest.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/module/webservices/rest/help")
public class HelpController {

	@RequestMapping(method=RequestMethod.GET)
	public void showPage(ModelMap map) {
		
		// TODO put content into map about controller annotations and resource views
	}
}
