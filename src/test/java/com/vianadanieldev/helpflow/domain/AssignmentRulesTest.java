package com.vianadanieldev.helpflow.domain;

import com.vianadanieldev.helpflow.domain.model.*;
import com.vianadanieldev.helpflow.domain.model.AssignmentRules.Decision;
import org.junit.jupiter.api.Test;

import static com.vianadanieldev.helpflow.support.TestData.*;
import static org.assertj.core.api.Assertions.assertThat;

class AssignmentRulesTest {

    private final AssignmentRules rules = new AssignmentRules(3);

    private Decision evaluate(Ticket ticket, Technician tech, int load, boolean available, int minutes) {
        return rules.evaluate(ticket, tech, load, new DispatchSuggestion(tech.getId(), available, minutes), NOW);
    }

    @Test
    void acceptsCompatibleTechnicianWithinLoadAndSla() {
        Ticket t = ticket(TicketCategory.NETWORK, TicketPriority.MEDIUM, "BOG");
        Technician tech = technician(Specialty.NETWORK, SupportProfile.LEVEL_1, "BOG");
        assertThat(evaluate(t, tech, 1, true, 60).eligible()).isTrue();
    }

    @Test
    void rejectsIncompatibleSpecialty() {
        Ticket t = ticket(TicketCategory.NETWORK, TicketPriority.MEDIUM, "BOG");
        Technician tech = technician(Specialty.HARDWARE, SupportProfile.LEVEL_2, "BOG");
        assertThat(evaluate(t, tech, 0, true, 60).eligible()).isFalse();
    }

    @Test
    void generalistCoversAnyCategory() {
        Ticket t = ticket(TicketCategory.OTHER, TicketPriority.MEDIUM, "BOG");
        Technician tech = technician(Specialty.GENERALIST, SupportProfile.LEVEL_1, "BOG");
        assertThat(evaluate(t, tech, 0, true, 60).eligible()).isTrue();
    }

    @Test
    void rejectsTechnicianAtMaximumLoad() {
        Ticket t = ticket(TicketCategory.NETWORK, TicketPriority.MEDIUM, "BOG");
        Technician tech = technician(Specialty.NETWORK, SupportProfile.LEVEL_2, "BOG");
        Decision d = evaluate(t, tech, 3, true, 60);
        assertThat(d.eligible()).isFalse();
        assertThat(d.reason()).contains("maximum");
    }

    @Test
    void rejectsOtherHeadquartersUnlessCritical() {
        Technician tech = technician(Specialty.NETWORK, SupportProfile.LEVEL_2, "MDE");
        assertThat(evaluate(ticket(TicketCategory.NETWORK, TicketPriority.HIGH, "BOG"), tech, 0, true, 30)
                .eligible()).isFalse();
        assertThat(evaluate(ticket(TicketCategory.NETWORK, TicketPriority.CRITICAL, "BOG"), tech, 0, true, 30)
                .eligible()).isTrue();
    }

    @Test
    void criticalNeedsAtLeastLevel2() {
        Ticket t = ticket(TicketCategory.NETWORK, TicketPriority.CRITICAL, "BOG");
        Technician junior = technician(Specialty.NETWORK, SupportProfile.LEVEL_1, "BOG");
        assertThat(evaluate(t, junior, 0, true, 30).eligible()).isFalse();
    }

    @Test
    void rejectsWhenEstimateWouldMissSla() {
        Ticket t = ticket(TicketCategory.NETWORK, TicketPriority.CRITICAL, "BOG"); // 2h SLA
        Technician tech = technician(Specialty.NETWORK, SupportProfile.LEVEL_3, "BOG");
        assertThat(evaluate(t, tech, 0, true, 180).eligible()).isFalse();
    }

    @Test
    void rejectsUnavailableOrInactiveTechnician() {
        Ticket t = ticket(TicketCategory.NETWORK, TicketPriority.MEDIUM, "BOG");
        Technician tech = technician(Specialty.NETWORK, SupportProfile.LEVEL_2, "BOG");
        assertThat(evaluate(t, tech, 0, false, 30).eligible()).isFalse();
        tech.deactivate();
        assertThat(evaluate(t, tech, 0, true, 30).eligible()).isFalse();
    }
}
