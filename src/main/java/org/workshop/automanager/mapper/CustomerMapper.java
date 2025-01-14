package org.workshop.automanager.mapper;

import org.mapstruct.Mapper;
import org.workshop.automanager.dto.request.CustomerRequestDTO;
import org.workshop.automanager.dto.response.CustomerResponseDTO;
import org.workshop.automanager.model.CustomerEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerEntity toCustomerEntity(CustomerRequestDTO customerRequestDTO);

    CustomerResponseDTO toCustomerResponseDTO(CustomerEntity customerEntity);

    List<CustomerResponseDTO> toCustomerResponseDTOList(List<CustomerEntity> customerEntities);
}
