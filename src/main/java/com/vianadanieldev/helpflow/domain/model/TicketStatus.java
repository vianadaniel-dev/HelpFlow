package com.vianadanieldev.helpflow.domain.model;

public enum TicketStatus {
    OPEN, ASSIGNED, EN_PROGRESS, RESOLVED, CLOSED, CANCELED;

    public boolean canTransitionTo(TicketStatus target) {
        return switch (this) {
            case OPEN -> target == ASSIGNED || target == CANCELED;
            case ASSIGNED -> target == EN_PROGRESS || target == CANCELED;
            case EN_PROGRESS -> target == RESOLVED || target == CANCELED;
            case RESOLVED -> target == CLOSED;
            case CLOSED, CANCELED -> false;
        };
    }

    /** The SLA clock only matters while someone still has to work on the ticket. */
    public boolean isSlaTracked() {
        return this == OPEN || this == ASSIGNED || this == EN_PROGRESS;
    }
}
