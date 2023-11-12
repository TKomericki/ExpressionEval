package com.lw.expressioneval.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

public class EntityNotFoundException extends ErrorResponseException {
    public EntityNotFoundException(Class<?> entityClass, Long id) {
        this(entityClass.getSimpleName(), id);
    }

    public EntityNotFoundException(String entityName, Long id) {
        super(HttpStatus.NOT_FOUND);

        this.getBody().setTitle(HttpStatus.NOT_FOUND.getReasonPhrase());
        this.getBody().setDetail(entityName + " with id " + id + " is not found.");
    }
}
