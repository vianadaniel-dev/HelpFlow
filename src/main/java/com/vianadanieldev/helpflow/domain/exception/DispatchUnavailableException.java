package com.vianadanieldev.helpflow.domain.exception;

/** The technician-dispatch-service could not be reached or answered with an error. */
public class DispatchUnavailableException extends DomainException {
    public DispatchUnavailableException(String message, Throwable cause) { super(message, cause); }
}
