package com.vianadanieldev.helpflow.application.service;

import com.vianadanieldev.helpflow.application.port.in.RegisterTicketUseCase;
import com.vianadanieldev.helpflow.application.port.out.AssignmentRepositoryPort;
import com.vianadanieldev.helpflow.application.port.out.TechnicianDispatchPort;
import com.vianadanieldev.helpflow.application.port.out.TechnicianDispatchPort.DispatchRequest;
import com.vianadanieldev.helpflow.application.port.out.TechnicianRepositoryPort;
import com.vianadanieldev.helpflow.application.port.out.TicketHistoryRepositoryPort;
import com.vianadanieldev.helpflow.application.port.out.TicketRepositoryPort;
import com.vianadanieldev.helpflow.application.port.out.UserRepositoryPort;
import com.vianadanieldev.helpflow.domain.exception.DispatchUnavailableException;
import com.vianadanieldev.helpflow.domain.exception.ResourceNotFoundException;
import com.vianadanieldev.helpflow.domain.model.Assignment;
import com.vianadanieldev.helpflow.domain.model.AssignmentRules;
import com.vianadanieldev.helpflow.domain.model.AssignmentRules.Decision;
import com.vianadanieldev.helpflow.domain.model.DispatchSuggestion;
import com.vianadanieldev.helpflow.domain.model.HistoryAction;
import com.vianadanieldev.helpflow.domain.model.SlaPolicy;
import com.vianadanieldev.helpflow.domain.model.Technician;
import com.vianadanieldev.helpflow.domain.model.Ticket;
import com.vianadanieldev.helpflow.domain.model.TicketHistory;
import com.vianadanieldev.helpflow.domain.model.TicketStatus;
import com.vianadanieldev.helpflow.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Registers a ticket and tries the initial assignment.
 * Everything is persisted inside ONE transaction: ticket + assignment + history rows commit or roll back together.
 * If no technician fits (or the dispatch service is down) the ticket is still created and stays OPEN.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RegisterTicketService implements RegisterTicketUseCase {

    // Field order = constructor order (used by the unit tests).
    private final UserRepositoryPort users;
    private final TechnicianRepositoryPort technicians;
    private final TicketRepositoryPort tickets;
    private final AssignmentRepositoryPort assignments;
    private final TicketHistoryRepositoryPort histories;
    private final TechnicianDispatchPort dispatch;
    private final AssignmentRules rules;
    private final SlaPolicy slaPolicy;
    private final Clock clock;

    @Override
    public RegisterTicketResult register(RegisterTicketCommand c) {
        User requester = users.findById(c.requesterId())
                .orElseThrow(() -> new ResourceNotFoundException("User", c.requesterId()));
        requester.ensureCanRegisterTickets();

        Instant now = Instant.now(clock);
        Ticket ticket = Ticket.open(requester.getId(), c.title(), c.description(), c.category(), c.priority(),
                c.headquarters(), c.affectedAssets(), slaPolicy, now);

        List<TicketHistory> trail = new ArrayList<>();
        trail.add(TicketHistory.record(ticket.getId(), HistoryAction.CREATED, null, TicketStatus.OPEN,
                "Ticket registered", requester.getId(), now));

        Assignment assignment = tryInitialAssignment(ticket, now, trail);

        tickets.save(ticket);                       // ticket first: history and assignment reference it
        if (assignment != null) {
            assignments.save(assignment);
        }
        trail.forEach(histories::save);
        return new RegisterTicketResult(ticket, assignment);
    }

    private Assignment tryInitialAssignment(Ticket ticket, Instant now, List<TicketHistory> trail) {
        Optional<DispatchSuggestion> suggestion;
        try {
            suggestion = dispatch.suggest(new DispatchRequest(ticket.getId(), ticket.getCategory(),
                    ticket.getPriority(), ticket.getHeadquarters(), ticket.getSlaDeadline()));
        } catch (DispatchUnavailableException e) {
            log.warn("technician-dispatch-service unavailable for ticket {}: {}", ticket.getId(), e.getMessage());
            return defer(ticket, "Dispatch service unavailable", now, trail);
        }
        if (suggestion.isEmpty()) {
            return defer(ticket, "Dispatch service returned no candidate", now, trail);
        }

        DispatchSuggestion s = suggestion.get();
        Optional<Technician> found = technicians.findById(s.technicianId());
        if (found.isEmpty()) {
            return defer(ticket, "Suggested technician " + s.technicianId() + " is unknown", now, trail);
        }
        Technician technician = found.get();

        int load = assignments.countActiveByTechnicianId(technician.getId());
        Decision decision = rules.evaluate(ticket, technician, load, s, now);
        if (!decision.eligible()) {
            return defer(ticket, decision.reason(), now, trail);
        }

        ticket.assignTo(technician.getId());
        trail.add(TicketHistory.record(ticket.getId(), HistoryAction.ASSIGNED, TicketStatus.OPEN,
                TicketStatus.ASSIGNED,
                "Assigned to " + technician.getName() + " (estimated " + s.estimatedMinutes() + " min)",
                null, now));
        return Assignment.create(ticket.getId(), technician.getId(), s.estimatedMinutes(), now);
    }

    private Assignment defer(Ticket ticket, String reason, Instant now, List<TicketHistory> trail) {
        trail.add(TicketHistory.record(ticket.getId(), HistoryAction.ASSIGNMENT_DEFERRED, TicketStatus.OPEN,
                TicketStatus.OPEN, "Left OPEN: " + reason, null, now));
        return null;
    }
}
