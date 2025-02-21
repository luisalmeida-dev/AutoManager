package org.workshop.automanager.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.workshop.automanager.dto.request.ModelRequestDTO;
import org.workshop.automanager.dto.response.BrandresponseDTO;
import org.workshop.automanager.exception.AlreadyExistsException;
import org.workshop.automanager.mapper.ModelMapper;
import org.workshop.automanager.model.BrandEntity;
import org.workshop.automanager.model.ModelEntity;
import org.workshop.automanager.repository.ModelRepository;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ModelServiceTest {
    @Mock
    private ModelRepository modelRepository;

    @Mock
    private BrandService brandService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ModelService modelService;

    @Test
    void createWhenModelAlreadyExistsTest(){
        //Arrange
        ModelRequestDTO request = new ModelRequestDTO("Chevrolet", 1);

        //Act
        when(modelRepository.existsByNameAndBrandId(request.getName(), request.getBrandId())).thenReturn(true);
        when(brandService.getBrandNameById(request.getBrandId())).thenReturn("Teste");

        //Assert
        AlreadyExistsException exception = Assertions.assertThrows(AlreadyExistsException.class, () -> {
            modelService.create(request);
        });
        Assertions.assertEquals("O modelo Chevrolet da marca Teste já existe.", exception.getMessage());
        verify(modelRepository, never()).save(any());
    }

    @Test
    void createSuccessfully(){
        //Arrange
        ModelRequestDTO request = new ModelRequestDTO("Chevrolet", 1);
        ModelEntity entity = new ModelEntity();
        entity.setBrandId(1);
        entity.setName("Chevrolet");

        BrandresponseDTO response = new BrandresponseDTO();
        response.setId(1);
        response.setName("Chevrolet");

        //Act
        when(modelRepository.existsByNameAndBrandId(request.getName(), request.getBrandId())).thenReturn(false);
        when(modelMapper.toModelEntity(request)).thenReturn(entity);
        when(brandService.getBrand(request.getBrandId())).thenReturn(response);
        modelService.create(request);

        //Assert
        verify(modelRepository, times(1)).save(entity);
    }
}
