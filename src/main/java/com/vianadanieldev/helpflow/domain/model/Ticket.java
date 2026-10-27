package com.vianadanieldev.helpflow.domain.model;

import com.vianadanieldev.helpflow.domain.exception.BusinessRuleException;
import com.vianadanieldev.helpflow.domain.exception.InvalidStateTransitionException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Aggregate root. All state changes go through its methods so the lifecycle rules live in one place. */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Ticket {

    private final UUID id;
    private final UUID requesterId;
    private final String title;
    private final String description;
    private final TicketCategory category;
    private final String headquarters;
    private final List<String> affectedAssets;
    private final Instant createdAt;
    private TicketPriority priority;
    private Instant slaDeadline;
    private TicketStatus status;
    private UUID assignedTechnicianId;
    private int escalationCount;

    public static Ticket open(UUID requesterId, String title, String description, TicketCategory category,
                              TicketPriority priority, String headquarters, List<String> affectedAssets,
                              SlaPolicy slaPolicy, Instant now) {
        return Ticket.builder()
                .id(UUID.randomUUID())
                .requesterId(Guard.notNull(requesterId, "requesterId"))
                .title(Guard.notBlank(title, "title"))
                .description(Guard.notBlank(description, "description"))
                .category(Guard.notNull(category, "category"))
                .priority(Guard.notNull(priority, "priority"))
                .headquarters(Guard.notBlank(headquarters, "headquarters"))
                .affectedAssets(affectedAssets == null ? List.of() : List.copyOf(affectedAssets))
                .createdAt(now)
                .slaDeadline(slaPolicy.deadlineFor(priority, now))
                .status(TicketStatus.OPEN)
                .escalationCount(0)
                .build();
    }

    public void assignTo(UUID technicianId) {
        transitionTo(TicketStatus.ASSIGNED);
        this.assignedTechnicianId = Guard.notNull(technicianId, "technicianId");
    }

    public void transitionTo(TicketStatus target) {
        if (!status.canTransitionTo(target)) {
            throw new InvalidStateTransitionException(status, target);
        }
        this.status = target;
    }

    public boolean isSlaBreached(Instant now) {
        return status.isSlaTracked() && now.isAfter(slaDeadline);
    }

    /**
     * Raises the priority one level (CRITICAL stays CRITICAL) and restarts the SLA clock with the new priority.
     * @return the priority the ticket had before escalating
     */
    public TicketPriority escalate(SlaPolicy slaPolicy, Instant now) {
        if (!isSlaBreached(now)) {
            throw new BusinessRuleException("Ticket " + id + " has not breached its SLA");
        }
        TicketPriority previous = this.priority;
        this.priority = previous.next();
        this.slaDeadline = slaPolicy.deadlineFor(this.priority, now);
        this.escalationCount++;
        return previous;
    }
}
