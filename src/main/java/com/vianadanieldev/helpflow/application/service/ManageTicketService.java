package com.vianadanieldev.helpflow.application.service;

import com.vianadanieldev.helpflow.application.port.in.ManageTicketUseCase;
import com.vianadanieldev.helpflow.application.port.out.AssignmentRepositoryPort;
import com.vianadanieldev.helpflow.application.port.out.TicketHistoryRepositoryPort;
import com.vianadanieldev.helpflow.application.port.out.TicketRepositoryPort;
import com.vianadanieldev.helpflow.domain.exception.BusinessRuleException;
import com.vianadanieldev.helpflow.domain.exception.ResourceNotFoundException;
import com.vianadanieldev.helpflow.domain.model.Guard;
import com.vianadanieldev.helpflow.domain.model.HistoryAction;
import com.vianadanieldev.helpflow.domain.model.SlaPolicy;
import com.vianadanieldev.helpflow.domain.model.Ticket;
import com.vianadanieldev.helpflow.domain.model.TicketHistory;
import com.vianadanieldev.helpflow.domain.model.TicketPriority;
import com.vianadanieldev.helpflow.domain.model.TicketStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ManageTicketService implements ManageTicketUseCase {

    // Field order = constructor order (used by the unit tests).
    private final TicketRepositoryPort tickets;
    private final AssignmentRepositoryPort assignments;
    private final TicketHistoryRepositoryPort histories;
    private final SlaPolicy slaPolicy;
    private final Clock clock;

    @Override
    public Ticket changeStatus(ChangeTicketStatusCommand c) {
        TicketStatus target = Guard.notNull(c.targetStatus(), "targetStatus");
        if (target == TicketStatus.ASSIGNED) {
            throw new BusinessRuleException("ASSIGNED is set by the assignment process, not by a status change");
        }
        if (target == TicketStatus.RESOLVED || target == TicketStatus.CANCELED) {
            Guard.notBlank(c.observation(), "observation");
        }

        Ticket ticket = tickets.findById(c.ticketId())
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", c.ticketId()));
        Instant now = Instant.now(clock);
        TicketStatus from = ticket.getStatus();

        ticket.transitionTo(target);

        // The technician's load is freed as soon as the work is over.
        if (target == TicketStatus.RESOLVED || target == TicketStatus.CANCELED) {
            assignments.findActiveByTicketId(ticket.getId()).ifPresent(a -> {
                a.release(now);
                assignments.save(a);
            });
        }

        tickets.save(ticket);
        histories.save(TicketHistory.record(ticket.getId(), HistoryAction.STATUS_CHANGED, from, target,
                c.observation(), c.performedBy(), now));
        return ticket;
    }

    @Override
    public int escalateOverdueTickets() {
        Instant now = Instant.now(clock);
        List<Ticket> overdue = tickets.findSlaBreached(now);
        for (Ticket ticket : overdue) {
            TicketPriority previous = ticket.escalate(slaPolicy, now);
            tickets.save(ticket);
            histories.save(TicketHistory.record(ticket.getId(), HistoryAction.ESCALATED,
                    ticket.getStatus(), ticket.getStatus(),
                    "SLA breached: priority " + previous + " -> " + ticket.getPriority(), null, now));
        }
        return overdue.size();
    }
}
