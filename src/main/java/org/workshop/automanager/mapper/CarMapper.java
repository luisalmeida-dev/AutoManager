package org.workshop.automanager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.workshop.automanager.dto.request.CarRequestDTO;
import org.workshop.automanager.dto.response.CarResponseDTO;
import org.workshop.automanager.model.CarEntity;
import org.workshop.automanager.model.CustomerEntity;
import org.workshop.automanager.model.ModelEntity;

@Mapper(componentModel = "spring")
public interface CarMapper {
    CarResponseDTO toCarResponseDTO (CarEntity entity);

    @Mapping(source = "customer", target = "customer")
    @Mapping(source = "model", target = "model")
    CarEntity toCarEntity (CarRequestDTO dto, ModelEntity model, CustomerEntity customer);
}
