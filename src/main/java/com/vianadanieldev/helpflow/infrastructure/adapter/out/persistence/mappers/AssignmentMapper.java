package com.vianadanieldev.helpflow.infrastructure.adapter.out.persistence.mapper;

import com.vianadanieldev.helpflow.domain.model.Assignment;
import com.vianadanieldev.helpflow.infrastructure.adapter.out.persistence.entity.AssignmentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AssignmentMapper {
    Assignment toDomain(AssignmentEntity entity);
    AssignmentEntity toEntity(Assignment domain);
}