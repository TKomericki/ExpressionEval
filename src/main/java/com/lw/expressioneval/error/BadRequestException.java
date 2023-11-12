package com.lw.expressioneval.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

/**
 * Exception to be thrown when there are invalid data in the request.
 */
public class BadRequestException extends ErrorResponseException {
    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST);

        this.getBody().setTitle(HttpStatus.BAD_REQUEST.getReasonPhrase());
        this.getBody().setDetail(message);
    }
}
