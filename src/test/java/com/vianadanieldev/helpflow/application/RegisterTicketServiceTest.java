package com.vianadanieldev.helpflow.application;

import com.vianadanieldev.helpflow.application.port.in.RegisterTicketUseCase.RegisterTicketCommand;
import com.vianadanieldev.helpflow.application.port.in.RegisterTicketUseCase.RegisterTicketResult;
import com.vianadanieldev.helpflow.application.port.out.*;
import com.vianadanieldev.helpflow.application.service.RegisterTicketService;
import com.vianadanieldev.helpflow.domain.exception.DispatchUnavailableException;
import com.vianadanieldev.helpflow.domain.exception.InactiveUserException;
import com.vianadanieldev.helpflow.domain.exception.ResourceNotFoundException;
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
class RegisterTicketServiceTest {

    @Mock UserRepositoryPort users;
    @Mock TechnicianRepositoryPort technicians;
    @Mock TicketRepositoryPort tickets;
    @Mock AssignmentRepositoryPort assignments;
    @Mock TicketHistoryRepositoryPort histories;
    @Mock TechnicianDispatchPort dispatch;

    RegisterTicketService service;

    @BeforeEach
    void setUp() {
        service = new RegisterTicketService(users, technicians, tickets, assignments, histories, dispatch,
                new AssignmentRules(3), SlaPolicy.defaults(), Clock.fixed(NOW, ZoneOffset.UTC));
    }

    private RegisterTicketCommand command(User requester) {
        return new RegisterTicketCommand(requester.getId(), "VPN down", "Cannot connect",
                TicketCategory.NETWORK, TicketPriority.MEDIUM, "BOG", List.of("LAPTOP-77"));
    }

    private List<HistoryAction> savedHistoryActions(int expectedEntries) {
        ArgumentCaptor<TicketHistory> captor = ArgumentCaptor.forClass(TicketHistory.class);
        verify(histories, times(expectedEntries)).save(captor.capture());
        return captor.getAllValues().stream().map(TicketHistory::getAction).toList();
    }

    @Test
    void assignsTicketWhenSuggestedTechnicianPassesAllRules() {
        User user = activeUser();
        Technician tech = technician(Specialty.NETWORK, SupportProfile.LEVEL_2, "BOG");
        when(users.findById(user.getId())).thenReturn(Optional.of(user));
        when(dispatch.suggest(any())).thenReturn(Optional.of(new DispatchSuggestion(tech.getId(), true, 60)));
        when(technicians.findById(tech.getId())).thenReturn(Optional.of(tech));
        when(assignments.countActiveByTechnicianId(tech.getId())).thenReturn(1);

        RegisterTicketResult result = service.register(command(user));

        assertThat(result.isAssigned()).isTrue();
        assertThat(result.ticket().getStatus()).isEqualTo(TicketStatus.ASSIGNED);
        assertThat(result.ticket().getAssignedTechnicianId()).isEqualTo(tech.getId());
        assertThat(result.ticket().getSlaDeadline()).isEqualTo(NOW.plus(Duration.ofHours(24)));
        verify(tickets).save(result.ticket());
        verify(assignments).save(result.assignment());
        assertThat(savedHistoryActions(2)).containsExactly(HistoryAction.CREATED, HistoryAction.ASSIGNED);
    }

    @Test
    void keepsTicketOpenWhenDispatchServiceIsDown() {
        User user = activeUser();
        when(users.findById(user.getId())).thenReturn(Optional.of(user));
        when(dispatch.suggest(any())).thenThrow(new DispatchUnavailableException("timeout", null));

        RegisterTicketResult result = service.register(command(user));

        assertThat(result.isAssigned()).isFalse();
        assertThat(result.ticket().getStatus()).isEqualTo(TicketStatus.OPEN);
        verify(tickets).save(result.ticket());
        verify(assignments, never()).save(any());
        verifyNoInteractions(technicians);
        assertThat(savedHistoryActions(2)).containsExactly(HistoryAction.CREATED, HistoryAction.ASSIGNMENT_DEFERRED);
    }

    @Test
    void keepsTicketOpenWhenDispatchHasNoCandidate() {
        User user = activeUser();
        when(users.findById(user.getId())).thenReturn(Optional.of(user));
        when(dispatch.suggest(any())).thenReturn(Optional.empty());

        RegisterTicketResult result = service.register(command(user));

        assertThat(result.ticket().getStatus()).isEqualTo(TicketStatus.OPEN);
        verify(assignments, never()).save(any());
    }

    @Test
    void keepsTicketOpenWhenTechnicianIsAtMaximumLoad() {
        User user = activeUser();
        Technician tech = technician(Specialty.NETWORK, SupportProfile.LEVEL_2, "BOG");
        when(users.findById(user.getId())).thenReturn(Optional.of(user));
        when(dispatch.suggest(any())).thenReturn(Optional.of(new DispatchSuggestion(tech.getId(), true, 60)));
        when(technicians.findById(tech.getId())).thenReturn(Optional.of(tech));
        when(assignments.countActiveByTechnicianId(tech.getId())).thenReturn(3);

        RegisterTicketResult result = service.register(command(user));

        assertThat(result.isAssigned()).isFalse();
        assertThat(result.ticket().getStatus()).isEqualTo(TicketStatus.OPEN);
        ArgumentCaptor<TicketHistory> captor = ArgumentCaptor.forClass(TicketHistory.class);
        verify(histories, times(2)).save(captor.capture());
        assertThat(captor.getAllValues().get(1).getObservation()).contains("maximum");
    }

    @Test
    void rejectsInactiveRequesterWithoutSavingAnything() {
        User user = inactiveUser();
        when(users.findById(user.getId())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> service.register(command(user))).isInstanceOf(InactiveUserException.class);

        verifyNoInteractions(dispatch, tickets, assignments, histories);
    }

    @Test
    void failsWhenRequesterDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(users.findById(id)).thenReturn(Optional.empty());
        var cmd = new RegisterTicketCommand(id, "t", "d", TicketCategory.OTHER, TicketPriority.LOW, "BOG", null);

        assertThatThrownBy(() -> service.register(cmd)).isInstanceOf(ResourceNotFoundException.class);
        verifyNoInteractions(dispatch, tickets);
    }
}
