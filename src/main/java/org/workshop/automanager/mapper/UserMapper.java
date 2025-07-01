package org.workshop.automanager.mapper;

import org.mapstruct.Mapper;
import org.workshop.automanager.dto.request.UserRequestDTO;
import org.workshop.automanager.dto.response.UserResponseDTO;
import org.workshop.automanager.model.UserEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserEntity toUserEntity(UserRequestDTO request);

    UserResponseDTO toUserResponseDTO(UserEntity entity);

    List<UserResponseDTO> toUserResponseDTOList(List<UserEntity> entities);
}
