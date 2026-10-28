package com.vianadanieldev.helpflow.application.port.in;

import com.vianadanieldev.helpflow.domain.model.Assignment;
import com.vianadanieldev.helpflow.domain.model.Ticket;
import com.vianadanieldev.helpflow.domain.model.TicketCategory;
import com.vianadanieldev.helpflow.domain.model.TicketPriority;

import java.util.List;
import java.util.UUID;

public interface RegisterTicketUseCase {

    RegisterTicketResult register(RegisterTicketCommand command);

    record RegisterTicketCommand(UUID requesterId, String title, String description, TicketCategory category,
                                 TicketPriority priority, String headquarters, List<String> affectedAssets) {}

    /** assignment is null when the ticket stayed OPEN (no suitable technician or dispatch service down). */
    record RegisterTicketResult(Ticket ticket, Assignment assignment) {
        public boolean isAssigned() { return assignment != null; }
    }
}
