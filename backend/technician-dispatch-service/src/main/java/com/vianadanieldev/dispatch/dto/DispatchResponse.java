package com.vianadanieldev.dispatch.dto;

import java.util.UUID;

public record DispatchResponse(
        Object ticketId,
        UUID tecnicoId,
        String disponibilidad,
        int tiempoEstimadoHoras
) {
}
