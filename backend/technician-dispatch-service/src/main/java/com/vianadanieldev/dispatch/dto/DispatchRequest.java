package com.vianadanieldev.dispatch.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.util.UUID;

public record DispatchRequest(
        Object ticketId,

        @JsonAlias({"category", "categoria"})
        String categoria,

        @JsonAlias({"priority", "prioridad"})
        String prioridad,

        @JsonAlias({"headquarters", "sede"})
        String sede
) {
    public String getNormalizedCategory() {
        if (categoria == null) return "GENERAL";
        return categoria.trim().toUpperCase();
    }

    public String getNormalizedPriority() {
        if (prioridad == null) return "MEDIA";
        return prioridad.trim().toUpperCase();
    }
}
