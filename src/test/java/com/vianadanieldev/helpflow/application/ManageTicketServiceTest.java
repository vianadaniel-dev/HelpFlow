package com.vianadanieldev.helpflow.application;

import com.vianadanieldev.helpflow.application.port.in.ManageTicketUseCase.ChangeTicketStatusCommand;
import com.vianadanieldev.helpflow.application.port.out.AssignmentRepositoryPort;
import com.vianadanieldev.helpflow.application.port.out.TicketHistoryRepositoryPort;
import com.vianadanieldev.helpflow.application.port.out.TicketRepositoryPort;
import com.vianadanieldev.helpflow.application.service.ManageTicketService;
import com.vianadanieldev.helpflow.domain.exception.InvalidDataException;
import com.vianadanieldev.helpflow.domain.exception.InvalidStateTransitionException;
import com.vianadanieldev.helpflow.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Duration;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.vianadanieldev.helpflow.support.TestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManageTicketServiceTest {

    @Mock TicketRepositoryPort tickets;
    @Mock AssignmentRepositoryPort assignments;
    @Mock TicketHistoryRepositoryPort histories;

    ManageTicketService service;
    final UUID performer = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new ManageTicketService(tickets, assignments, histories,
                SlaPolicy.defaults(), Clock.fixed(NOW, ZoneOffset.UTC));
    }

    private Ticket inProgressTicket(UUID technicianId) {
        Ticket t = ticket(TicketCategory.NETWORK, TicketPriority.MEDIUM, "BOG");
        t.assignTo(technicianId);
        t.transitionTo(TicketStatus.EN_PROGRESS);
        return t;
    }

    @Test
    void resolvingReleasesTheAssignmentAndWritesHistory() {
        UUID techId = UUID.randomUUID();
        Ticket t = inProgressTicket(techId);
        Assignment a = Assignment.create(t.getId(), techId, 60, NOW.minusSeconds(3600));
        when(tickets.findById(t.getId())).thenReturn(Optional.of(t));
        when(assignments.findActiveByTicketId(t.getId())).thenReturn(Optional.of(a));

        service.changeStatus(new ChangeTicketStatusCommand(t.getId(), TicketStatus.RESOLVED, "Replaced switch", performer));

        assertThat(t.getStatus()).isEqualTo(TicketStatus.RESOLVED);
        assertThat(a.isActive()).isFalse();
        verify(assignments).save(a);
        verify(tickets).save(t);
        ArgumentCaptor<TicketHistory> captor = ArgumentCaptor.forClass(TicketHistory.class);
        verify(histories).save(captor.capture());
        TicketHistory h = captor.getValue();
        assertThat(h.getFromStatus()).isEqualTo(TicketStatus.EN_PROGRESS);
        assertThat(h.getToStatus()).isEqualTo(TicketStatus.RESOLVED);
        assertThat(h.getObservation()).isEqualTo("Replaced switch");
        assertThat(h.getPerformedBy()).isEqualTo(performer);
    }

    @Test
    void rejectsInvalidTransitionWithoutSaving() {
        Ticket t = ticket(TicketCategory.NETWORK, TicketPriority.LOW, "BOG");
        when(tickets.findById(t.getId())).thenReturn(Optional.of(t));

        assertThatThrownBy(() -> service.changeStatus(
                new ChangeTicketStatusCommand(t.getId(), TicketStatus.CLOSED, "x", performer)))
                .isInstanceOf(InvalidStateTransitionException.class);

        verify(tickets, never()).save(any());
        verifyNoInteractions(assignments, histories);
    }

    @Test
    void cancelingRequiresAnObservation() {
        assertThatThrownBy(() -> service.changeStatus(
                new ChangeTicketStatusCommand(UUID.randomUUID(), TicketStatus.CANCELED, "  ", performer)))
                .isInstanceOf(InvalidDataException.class);
        verifyNoInteractions(tickets);
    }

    @Test
    void escalatesBreachedTicketsAndLogsIt() {
        Ticket overdue = ticket(TicketCategory.NETWORK, TicketPriority.LOW, "BOG", NOW.minus(Duration.ofHours(100)));
        when(tickets.findSlaBreached(NOW)).thenReturn(List.of(overdue));

        int escalated = service.escalateOverdueTickets();

        assertThat(escalated).isEqualTo(1);
        assertThat(overdue.getPriority()).isEqualTo(TicketPriority.MEDIUM);
        assertThat(overdue.getSlaDeadline()).isEqualTo(NOW.plus(Duration.ofHours(24)));
        verify(tickets).save(overdue);
        ArgumentCaptor<TicketHistory> captor = ArgumentCaptor.forClass(TicketHistory.class);
        verify(histories).save(captor.capture());
        assertThat(captor.getValue().getAction()).isEqualTo(HistoryAction.ESCALATED);
        assertThat(captor.getValue().getPerformedBy()).isNull();
    }

    @Test
    void escalationDoesNothingWhenNothingIsOverdue() {
        when(tickets.findSlaBreached(NOW)).thenReturn(List.of());
        assertThat(service.escalateOverdueTickets()).isZero();
        verifyNoInteractions(histories);
    }
}
