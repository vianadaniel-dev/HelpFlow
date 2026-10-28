package com.vianadanieldev.helpflow.domain.exception;

public class DuplicateResourceException extends DomainException {
    public DuplicateResourceException(String resource, String field, String value) {
        super(resource + " already exists with " + field + " = " + value);
    }
}
