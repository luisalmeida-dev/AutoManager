package org.workshop.automanager.mapper;

import org.mapstruct.Mapper;
import org.workshop.automanager.dto.request.ModelRequestDTO;
import org.workshop.automanager.dto.response.ModelResponseDTO;
import org.workshop.automanager.model.ModelEntity;
import org.workshop.automanager.service.BrandService;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ModelMapper {
    // Método para mapear ModelRequestDTO para ModelEntity
    ModelEntity toModelEntity(ModelRequestDTO modelRequestDTO);

    // Método default para mapear ModelEntity para ModelResponseDTO
    default ModelResponseDTO toModelResponseDTO(ModelEntity modelEntity) {
        return new ModelResponseDTO(modelEntity.getId(), modelEntity.getName(), modelEntity.getBrand().getName());
    }


    // Método default para mapear uma lista de ModelEntity para uma lista de ModelResponseDTO
    default List<ModelResponseDTO> toModelResponseDTOList(List<ModelEntity> modelEntities) {
        return modelEntities.stream()
                .map(this::toModelResponseDTO)
                .toList();
    }
}
