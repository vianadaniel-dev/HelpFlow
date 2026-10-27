package com.vianadanieldev.helpflow.application.port.out;

import com.vianadanieldev.helpflow.domain.model.TicketHistory;

import java.util.List;
import java.util.UUID;

public interface TicketHistoryRepositoryPort {
    void save(TicketHistory entry);
    List<TicketHistory> findByTicketId(UUID ticketId);
}
