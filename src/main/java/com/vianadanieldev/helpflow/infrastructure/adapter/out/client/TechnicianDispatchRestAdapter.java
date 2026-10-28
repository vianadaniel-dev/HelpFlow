package com.vianadanieldev.helpflow.infrastructure.adapter.out.client;

import com.vianadanieldev.helpflow.application.port.out.TechnicianDispatchPort;
import com.vianadanieldev.helpflow.domain.exception.DispatchUnavailableException;
import com.vianadanieldev.helpflow.domain.model.DispatchSuggestion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Slf4j
@Component
public class TechnicianDispatchRestAdapter implements TechnicianDispatchPort {

    private final RestClient restClient;

    public TechnicianDispatchRestAdapter(RestClient.Builder restClientBuilder,
                                         @Value("${helpflow.dispatch-service.url:http://localhost:8081}") String dispatchServiceUrl) {
        this.restClient = restClientBuilder.baseUrl(dispatchServiceUrl).build();
    }

    @Override
    public Optional<DispatchSuggestion> suggest(DispatchRequest request) {
        try {
            DispatchSuggestionResponse response = restClient.post()
                    .uri("/technician-dispatch")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(DispatchSuggestionResponse.class);

            if (response == null || response.tecnicoId() == null) {
                return Optional.empty();
            }

            return Optional.of(new DispatchSuggestion(
                    response.tecnicoId(),
                    response.disponibilidad().equals("ALTA") || response.disponibilidad().equals("MEDIA"),
                    response.tiempoEstimadoHoras() * 60
            ));

        } catch (Exception e) {
            log.error("Failed to call technician-dispatch-service", e);
            throw new DispatchUnavailableException("Technician Dispatch Service is down or returned an error");
        }
    }

    private record DispatchSuggestionResponse(
            java.util.UUID ticketId,
            java.util.UUID tecnicoId,
            String disponibilidad,
            int tiempoEstimadoHoras
    ) {}
}