package com.vianadanieldev.helpflow.infrastructure.config;

import com.vianadanieldev.helpflow.domain.model.AssignmentRules;
import com.vianadanieldev.helpflow.domain.model.CorporateEmailPolicy;
import com.vianadanieldev.helpflow.domain.model.SlaPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.util.List;

/** Wires the framework-free domain classes as Spring beans. */
@Configuration
public class BeanConfig {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    SlaPolicy slaPolicy() {
        return SlaPolicy.defaults();
    }

    @Bean
    CorporateEmailPolicy corporateEmailPolicy(@Value("${helpflow.corporate-email-domains}") List<String> domains) {
        return new CorporateEmailPolicy(domains);
    }

    @Bean
    AssignmentRules assignmentRules(@Value("${helpflow.assignment.max-active-tickets:5}") int maxActiveTickets) {
        return new AssignmentRules(maxActiveTickets);
    }
}
