package com.vianadanieldev.helpflow.domain.exception;

public class InvalidCorporateEmailException extends BusinessRuleException {
    public InvalidCorporateEmailException(String email) {
        super("Not a valid corporate email: " + email);
    }
}
