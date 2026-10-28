package com.vianadanieldev.helpflow.infrastructure.adapter.in.web;

import com.vianadanieldev.helpflow.application.port.in.ManageTicketUseCase;
import com.vianadanieldev.helpflow.application.port.in.RegisterTicketUseCase;
import com.vianadanieldev.helpflow.domain.model.TicketStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final RegisterTicketUseCase registerTicketUseCase;
    private final ManageTicketUseCase manageTicketUseCase;

    @PostMapping
    public ResponseEntity<RegisterTicketUseCase.RegisterTicketResult> createTicket(
            @Valid @RequestBody RegisterTicketUseCase.RegisterTicketCommand command) {

        var result = registerTicketUseCase.register(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateStatusRequest request) {

        manageTicketUseCase.changeStatus(new ManageTicketUseCase.ChangeTicketStatusCommand(
                id,
                request.targetStatus(),
                request.observation(),
                request.performedBy()
        ));

        return ResponseEntity.noContent().build();
    }

    public record UpdateStatusRequest(
            @NotNull TicketStatus targetStatus,
            @NotBlank String observation,
            @NotNull UUID performedBy
    ) {}
}