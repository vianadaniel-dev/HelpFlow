package com.vianadanieldev.helpflow.domain.model;

public enum Specialty {
    HARDWARE, SOFTWARE, NETWORK, ACCESS, GENERALIST;

    public boolean supports(TicketCategory category) {
        return switch (this) {
            case GENERALIST -> true;
            case HARDWARE -> category == TicketCategory.HARDWARE;
            case SOFTWARE -> category == TicketCategory.SOFTWARE;
            case NETWORK -> category == TicketCategory.NETWORK;
            case ACCESS -> category == TicketCategory.ACCESS;
        };
    }
}
