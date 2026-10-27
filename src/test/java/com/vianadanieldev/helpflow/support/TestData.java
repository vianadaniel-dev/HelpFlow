package com.vianadanieldev.helpflow.support;

import com.vianadanieldev.helpflow.domain.model.*;

import java.time.Instant;
import java.util.List;

public final class TestData {

    public static final Instant NOW = Instant.parse("2026-10-27T10:00:00Z");

    private TestData() {}

    public static User activeUser() {
        return User.register("1001", "Ana Perez", "ana.perez@helpflow.com", "BOG", "Finance");
    }

    public static User inactiveUser() {
        User user = activeUser();
        user.deactivate();
        return user;
    }

    public static Technician technician(Specialty specialty, SupportProfile profile, String headquarters) {
        return Technician.register("2001", "Luis Gomez", "luis.gomez@helpflow.com",
                headquarters, specialty, profile);
    }

    public static Ticket ticket(TicketCategory category, TicketPriority priority, String headquarters, Instant createdAt) {
        return Ticket.open(activeUser().getId(), "Printer down", "Cannot print", category, priority,
                headquarters, List.of("PRN-01"), SlaPolicy.defaults(), createdAt);
    }

    public static Ticket ticket(TicketCategory category, TicketPriority priority, String headquarters) {
        return ticket(category, priority, headquarters, NOW);
    }
}
