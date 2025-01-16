package org.workshop.automanager.mapper;

import org.mapstruct.Mapper;
import org.workshop.automanager.dto.request.BrandRequestDTO;
import org.workshop.automanager.dto.response.BrandresponseDTO;
import org.workshop.automanager.model.BrandEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BrandMapper {
    BrandEntity toEntity(BrandRequestDTO request);

    List<BrandresponseDTO> toBrandResponseList(List<BrandEntity> entities);

    BrandresponseDTO toBrandResponseDTO(BrandEntity brandEntity);
}
