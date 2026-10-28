package com.vianadanieldev.helpflow.application.port.in;

import com.vianadanieldev.helpflow.domain.model.Ticket;
import com.vianadanieldev.helpflow.domain.model.TicketStatus;

import java.util.UUID;

public interface ManageTicketUseCase {

    Ticket changeStatus(ChangeTicketStatusCommand command);

    /** @return how many tickets were escalated */
    int escalateOverdueTickets();

    record ChangeTicketStatusCommand(UUID ticketId, TicketStatus targetStatus,
                                     String observation, UUID performedBy) {}
}
