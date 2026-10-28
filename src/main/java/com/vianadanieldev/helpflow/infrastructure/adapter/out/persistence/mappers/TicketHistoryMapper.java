package com.vianadanieldev.helpflow.infrastructure.adapter.out.persistence.mapper;

import com.vianadanieldev.helpflow.domain.model.TicketHistory;
import com.vianadanieldev.helpflow.infrastructure.adapter.out.persistence.entity.TicketHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TicketHistoryMapper {
    TicketHistory toDomain(TicketHistoryEntity entity);
    TicketHistoryEntity toEntity(TicketHistory domain);
}