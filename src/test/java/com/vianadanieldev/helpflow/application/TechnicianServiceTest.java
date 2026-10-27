package com.vianadanieldev.helpflow.application;

import com.vianadanieldev.helpflow.application.port.in.ManageTechnicianUseCase.RegisterTechnicianCommand;
import com.vianadanieldev.helpflow.application.port.in.ManageTechnicianUseCase.UpdateTechnicianCommand;
import com.vianadanieldev.helpflow.application.port.out.TechnicianRepositoryPort;
import com.vianadanieldev.helpflow.application.service.TechnicianService;
import com.vianadanieldev.helpflow.domain.exception.DuplicateResourceException;
import com.vianadanieldev.helpflow.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.vianadanieldev.helpflow.support.TestData.technician;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TechnicianServiceTest {

    @Mock TechnicianRepositoryPort technicians;
    TechnicianService service;

    @BeforeEach
    void setUp() {
        service = new TechnicianService(technicians, new CorporateEmailPolicy(List.of("helpflow.com")));
    }

    @Test
    void registersTechnician() {
        var cmd = new RegisterTechnicianCommand("2001", "Luis Gomez", "luis.gomez@helpflow.com", "BOG",
                Specialty.NETWORK, SupportProfile.LEVEL_2);

        Technician t = service.register(cmd);

        assertThat(t.isActive()).isTrue();
        verify(technicians).save(t);
    }

    @Test
    void rejectsDuplicateDocument() {
        when(technicians.existsByDocument("2001")).thenReturn(true);
        var cmd = new RegisterTechnicianCommand("2001", "Luis", "luis@helpflow.com", "BOG",
                Specialty.NETWORK, SupportProfile.LEVEL_2);

        assertThatThrownBy(() -> service.register(cmd)).isInstanceOf(DuplicateResourceException.class);
        verify(technicians, never()).save(any());
    }

    @Test
    void updatesSpecialtyAndSupportProfile() {
        Technician t = technician(Specialty.NETWORK, SupportProfile.LEVEL_1, "BOG");
        when(technicians.findById(t.getId())).thenReturn(Optional.of(t));

        service.update(t.getId(), new UpdateTechnicianCommand("Luis Gomez", "BOG",
                Specialty.SOFTWARE, SupportProfile.LEVEL_3));

        assertThat(t.getSpecialty()).isEqualTo(Specialty.SOFTWARE);
        assertThat(t.getSupportProfile()).isEqualTo(SupportProfile.LEVEL_3);
        verify(technicians).save(t);
    }
}
