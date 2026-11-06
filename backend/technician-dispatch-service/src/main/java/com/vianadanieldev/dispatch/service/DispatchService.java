package com.vianadanieldev.dispatch.service;

import com.vianadanieldev.dispatch.dto.DispatchRequest;
import com.vianadanieldev.dispatch.dto.DispatchResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class DispatchService {

    private static final UUID TECH_SOFTWARE = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID TECH_NETWORK = UUID.fromString("33333333-3333-3333-3333-333333333333");

    public DispatchResponse dispatch(DispatchRequest request) {
        String category = request.getNormalizedCategory();
        String priority = request.getNormalizedPriority();

        log.info("Evaluating dispatch for ticketId: {}, category: {}, priority: {}",
                request.ticketId(), category, priority);

        UUID selectedTech;
        int estimatedHours;

        if (category.contains("RED") || category.contains("NETWORK")) {
            selectedTech = TECH_NETWORK;
            estimatedHours = switch (priority) {
                case "CRITICA", "CRÍTICA", "CRITICAL" -> 1;
                case "ALTA", "HIGH" -> 2;
                case "MEDIA", "MEDIUM" -> 4;
                default -> 8;
            };
        } else if (category.contains("SOFTWARE") || category.contains("ACCESOS") || category.contains("ACCESS")) {
            selectedTech = TECH_SOFTWARE;
            estimatedHours = switch (priority) {
                case "CRITICA", "CRÍTICA", "CRITICAL" -> 1;
                case "ALTA", "HIGH" -> 2;
                case "MEDIA", "MEDIUM" -> 4;
                default -> 8;
            };
        } else {
            selectedTech = TECH_SOFTWARE;
            estimatedHours = switch (priority) {
                case "CRITICA", "CRÍTICA", "CRITICAL" -> 1;
                case "ALTA", "HIGH" -> 3;
                case "MEDIA", "MEDIUM" -> 6;
                default -> 12;
            };
        }

        return new DispatchResponse(
                request.ticketId(),
                selectedTech,
                "ALTA",
                estimatedHours
        );
    }
}
