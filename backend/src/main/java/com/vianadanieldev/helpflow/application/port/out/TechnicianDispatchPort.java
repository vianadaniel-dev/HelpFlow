package com.vianadanieldev.helpflow.application.port.out;

import com.vianadanieldev.helpflow.domain.model.DispatchSuggestion;
import com.vianadanieldev.helpflow.domain.model.TicketCategory;
import com.vianadanieldev.helpflow.domain.model.TicketPriority;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/** Outbound port to technician-dispatch-service (implemented by a REST adapter). */
public interface TechnicianDispatchPort {

    /**
     * @return the suggestion, or empty when the service has no candidate
     * @throws com.vianadanieldev.helpflow.domain.exception.DispatchUnavailableException
     *         on timeouts, connection problems or 5xx answers
     */
    Optional<DispatchSuggestion> suggest(DispatchRequest request);

    record DispatchRequest(UUID ticketId, TicketCategory category, TicketPriority priority,
                           String headquarters, Instant slaDeadline) {}
}
