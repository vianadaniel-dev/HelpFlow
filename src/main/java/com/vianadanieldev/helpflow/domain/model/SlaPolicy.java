package com.vianadanieldev.helpflow.domain.model;

import java.time.Duration;
import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;

public class SlaPolicy {

    private final Map<TicketPriority, Duration> targets;

    public SlaPolicy(Map<TicketPriority, Duration> targets) {
        this.targets = new EnumMap<>(targets);
        for (TicketPriority p : TicketPriority.values()) {
            Guard.notNull(this.targets.get(p), "SLA for " + p);
        }
    }

    public static SlaPolicy defaults() {
        return new SlaPolicy(Map.of(
                TicketPriority.CRITICAL, Duration.ofHours(2),
                TicketPriority.HIGH, Duration.ofHours(8),
                TicketPriority.MEDIUM, Duration.ofHours(24),
                TicketPriority.LOW, Duration.ofHours(72)));
    }

    public Instant deadlineFor(TicketPriority priority, Instant from) {
        return from.plus(targets.get(priority));
    }
}
