package com.vianadanieldev.helpflow.domain;

import com.vianadanieldev.helpflow.domain.exception.BusinessRuleException;
import com.vianadanieldev.helpflow.domain.exception.InvalidStateTransitionException;
import com.vianadanieldev.helpflow.domain.model.*;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.UUID;

import static com.vianadanieldev.helpflow.support.TestData.NOW;
import static com.vianadanieldev.helpflow.support.TestData.ticket;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TicketTest {

    @Test
    void opensWithStatusOpenAndSlaFromPriority() {
        Ticket t = ticket(TicketCategory.NETWORK, TicketPriority.HIGH, "BOG");
        assertThat(t.getStatus()).isEqualTo(TicketStatus.OPEN);
        assertThat(t.getSlaDeadline()).isEqualTo(NOW.plus(Duration.ofHours(8)));
    }

    @Test
    void followsTheHappyPathLifecycle() {
        Ticket t = ticket(TicketCategory.NETWORK, TicketPriority.LOW, "BOG");
        UUID tech = UUID.randomUUID();
        t.assignTo(tech);
        t.transitionTo(TicketStatus.EN_PROGRESS);
        t.transitionTo(TicketStatus.RESOLVED);
        t.transitionTo(TicketStatus.CLOSED);
        assertThat(t.getStatus()).isEqualTo(TicketStatus.CLOSED);
        assertThat(t.getAssignedTechnicianId()).isEqualTo(tech);
    }

    @Test
    void rejectsSkippingStates() {
        Ticket t = ticket(TicketCategory.NETWORK, TicketPriority.LOW, "BOG");
        assertThatThrownBy(() -> t.transitionTo(TicketStatus.CLOSED))
                .isInstanceOf(InvalidStateTransitionException.class);
    }

    @Test
    void canceledTicketIsTerminal() {
        Ticket t = ticket(TicketCategory.NETWORK, TicketPriority.LOW, "BOG");
        t.transitionTo(TicketStatus.CANCELED);
        assertThatThrownBy(() -> t.transitionTo(TicketStatus.OPEN))
                .isInstanceOf(InvalidStateTransitionException.class);
    }

    @Test
    void resolvedTicketNeverBreachesSla() {
        Ticket t = ticket(TicketCategory.NETWORK, TicketPriority.CRITICAL, "BOG");
        t.assignTo(UUID.randomUUID());
        t.transitionTo(TicketStatus.EN_PROGRESS);
        t.transitionTo(TicketStatus.RESOLVED);
        assertThat(t.isSlaBreached(NOW.plus(Duration.ofDays(10)))).isFalse();
    }

    @Test
    void escalateRaisesPriorityAndRestartsSlaClock() {
        Ticket t = ticket(TicketCategory.NETWORK, TicketPriority.LOW, "BOG");
        var later = NOW.plus(Duration.ofHours(100));

        TicketPriority previous = t.escalate(SlaPolicy.defaults(), later);

        assertThat(previous).isEqualTo(TicketPriority.LOW);
        assertThat(t.getPriority()).isEqualTo(TicketPriority.MEDIUM);
        assertThat(t.getSlaDeadline()).isEqualTo(later.plus(Duration.ofHours(24)));
        assertThat(t.getEscalationCount()).isEqualTo(1);
    }

    @Test
    void escalateFailsWhenSlaIsStillRunning() {
        Ticket t = ticket(TicketCategory.NETWORK, TicketPriority.LOW, "BOG");
        assertThatThrownBy(() -> t.escalate(SlaPolicy.defaults(), NOW))
                .isInstanceOf(BusinessRuleException.class);
    }
}
