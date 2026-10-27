package com.vianadanieldev.helpflow.domain.model;

import java.time.Duration;
import java.time.Instant;

/**
 * Pure domain service: decides whether the technician suggested by technician-dispatch-service
 * may really take the ticket. No I/O, so it is trivially unit-testable.
 */
public class AssignmentRules {

    public record Decision(boolean eligible, String reason) {
        static Decision ok() { return new Decision(true, null); }
        static Decision rejected(String reason) { return new Decision(false, reason); }
    }

    private final int maxActiveTickets;

    public AssignmentRules(int maxActiveTickets) {
        if (maxActiveTickets < 1) {
            throw new IllegalArgumentException("maxActiveTickets must be >= 1");
        }
        this.maxActiveTickets = maxActiveTickets;
    }

    public Decision evaluate(Ticket ticket, Technician technician, int activeTickets,
                             DispatchSuggestion suggestion, Instant now) {
        if (!technician.isActive()) {
            return Decision.rejected("Technician is INACTIVE");
        }
        if (!suggestion.available()) {
            return Decision.rejected("Technician is not available");
        }
        if (!technician.getSpecialty().supports(ticket.getCategory())) {
            return Decision.rejected("Specialty " + technician.getSpecialty()
                    + " does not cover category " + ticket.getCategory());
        }
        if (activeTickets >= maxActiveTickets) {
            return Decision.rejected("Technician reached the maximum of " + maxActiveTickets + " active tickets");
        }
        boolean critical = ticket.getPriority() == TicketPriority.CRITICAL;
        // Same site is required, except for CRITICAL tickets where any site may respond.
        if (!critical && !technician.getHeadquarters().equalsIgnoreCase(ticket.getHeadquarters())) {
            return Decision.rejected("Technician works at " + technician.getHeadquarters()
                    + " but the ticket is at " + ticket.getHeadquarters());
        }
        if (critical && !technician.getSupportProfile().isAtLeast(SupportProfile.LEVEL_2)) {
            return Decision.rejected("CRITICAL tickets require support profile LEVEL_2 or higher");
        }
        Instant expectedFinish = now.plus(Duration.ofMinutes(suggestion.estimatedMinutes()));
        if (expectedFinish.isAfter(ticket.getSlaDeadline())) {
            return Decision.rejected("Estimated time (" + suggestion.estimatedMinutes()
                    + " min) would miss the SLA deadline");
        }
        return Decision.ok();
    }
}
