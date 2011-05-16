package org.openmrs.module.webservices.rest.web.response;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ResourceDoesNotSupportOperationException extends ResponseException {

    private static final long serialVersionUID = 1L;
    
    public ResourceDoesNotSupportOperationException() {
    	super();
    }
    
    public ResourceDoesNotSupportOperationException(String message, Throwable cause) {
    	super(message, cause);
    }

}
