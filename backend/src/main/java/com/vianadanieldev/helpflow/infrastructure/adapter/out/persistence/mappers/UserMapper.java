package com.vianadanieldev.helpflow.infrastructure.adapter.out.persistence.mapper;

import com.vianadanieldev.helpflow.domain.model.User;
import com.vianadanieldev.helpflow.infrastructure.adapter.out.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    User toDomain(UserEntity entity);
    UserEntity toEntity(User domain);
}