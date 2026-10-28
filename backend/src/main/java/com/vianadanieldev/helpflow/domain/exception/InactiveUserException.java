package com.vianadanieldev.helpflow.domain.exception;

import java.util.UUID;

public class InactiveUserException extends BusinessRuleException {
    public InactiveUserException(UUID userId) {
        super("User " + userId + " is INACTIVE and cannot register tickets");
    }
}
