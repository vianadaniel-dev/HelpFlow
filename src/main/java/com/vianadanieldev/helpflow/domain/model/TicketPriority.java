package com.vianadanieldev.helpflow.domain.model;

public enum TicketPriority {
    LOW, MEDIUM, HIGH, CRITICAL;

    /** Next priority level; CRITICAL stays CRITICAL. */
    public TicketPriority next() {
        return this == CRITICAL ? this : values()[ordinal() + 1];
    }
}
