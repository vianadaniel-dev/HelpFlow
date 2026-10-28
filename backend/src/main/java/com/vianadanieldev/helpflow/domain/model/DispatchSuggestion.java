package com.vianadanieldev.helpflow.domain.model;

import java.util.UUID;

/** What technician-dispatch-service answers: who, whether free, and how long it will take. */
public record DispatchSuggestion(UUID technicianId, boolean available, int estimatedMinutes) {}
