package com.vianadanieldev.helpflow.domain.exception;

import com.vianadanieldev.helpflow.domain.model.TicketStatus;

public class InvalidStateTransitionException extends BusinessRuleException {
    public InvalidStateTransitionException(TicketStatus from, TicketStatus to) {
        super("Invalid ticket transition: " + from + " -> " + to);
    }
}
