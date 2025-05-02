package org.workshop.automanager.mapper;

import org.mapstruct.Mapper;
import org.workshop.automanager.dto.request.UserRequestDTO;
import org.workshop.automanager.model.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserEntity toUserEntity(UserRequestDTO request);
}
