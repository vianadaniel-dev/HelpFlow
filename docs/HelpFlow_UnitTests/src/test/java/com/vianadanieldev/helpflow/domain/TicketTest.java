package com.vianadanieldev.helpflow.domain;

import com.vianadanieldev.helpflow.domain.model.TicketStatus;
import com.vianadanieldev.helpflow.domain.model.Ticket;
import org.junit.jupiter.api.Test;
import java.time.ZonedDateTime;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class TicketTest {

    @Test
    void testValidStatusTransition() {
        Ticket ticket = Ticket.builder()
                .id(UUID.randomUUID())
                .status(TicketStatus.OPEN)
                .createdAt(ZonedDateTime.now())
                .build();

        ticket.changeStatus(TicketStatus.EN_PROGRESS);
        assertEquals(TicketStatus.EN_PROGRESS, ticket.getStatus());
    }
}
