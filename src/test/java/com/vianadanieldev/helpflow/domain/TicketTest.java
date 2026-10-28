package com.vianadanieldev.helpflow.domain;

import com.vianadanieldev.helpflow.domain.model.TicketStatus;
import com.vianadanieldev.helpflow.domain.model.Ticket;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class TicketTest {

    @Test
    void testTicketCreation() {
        // Arrange
        UUID expectedId = UUID.randomUUID();
        Instant expectedDate = Instant.now();

        // Act
        Ticket ticket = Ticket.builder()
                .id(expectedId)
                .status(TicketStatus.OPEN)
                .createdAt(expectedDate)
                .build();

        // Assert
        assertNotNull(ticket);
        assertEquals(TicketStatus.OPEN, ticket.getStatus());
        assertEquals(expectedId, ticket.getId());
        assertEquals(expectedDate, ticket.getCreatedAt());
    }
}