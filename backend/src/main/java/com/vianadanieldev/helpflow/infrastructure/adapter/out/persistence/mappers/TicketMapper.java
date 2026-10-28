package com.vianadanieldev.helpflow.infrastructure.adapter.out.persistence.mapper;

import com.vianadanieldev.helpflow.domain.model.Ticket;
import com.vianadanieldev.helpflow.infrastructure.adapter.out.persistence.entity.TicketEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TicketMapper {
    Ticket toDomain(TicketEntity entity);
    TicketEntity toEntity(Ticket domain);
}