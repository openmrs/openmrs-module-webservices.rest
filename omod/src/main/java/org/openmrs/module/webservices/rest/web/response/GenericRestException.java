package org.openmrs.module.webservices.rest.web.response;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Generic exception, to be thrown if nothing more specific can be thrown. (Please try to throw a more
 * specif exception whenever possible.)
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class GenericRestException extends ResponseException {

    private static final long serialVersionUID = 1L;
    
    public GenericRestException() {
    	super();
    }
    
    public GenericRestException(String message, Throwable cause) {
    	super(message, cause);
    }

}
