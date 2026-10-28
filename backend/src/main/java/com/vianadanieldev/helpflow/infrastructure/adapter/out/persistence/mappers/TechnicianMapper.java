package com.vianadanieldev.helpflow.infrastructure.adapter.out.persistence.mapper;

import com.vianadanieldev.helpflow.domain.model.Technician;
import com.vianadanieldev.helpflow.infrastructure.adapter.out.persistence.entity.TechnicianEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TechnicianMapper {
    Technician toDomain(TechnicianEntity entity);
    TechnicianEntity toEntity(Technician domain);
}