package org.workshop.automanager.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.workshop.automanager.dto.request.ModelRequestDTO;
import org.workshop.automanager.dto.response.BrandresponseDTO;
import org.workshop.automanager.dto.response.ModelResponseDTO;
import org.workshop.automanager.exception.AlreadyExistsException;
import org.workshop.automanager.exception.InvalidArgumentException;
import org.workshop.automanager.exception.NotFoundException;
import org.workshop.automanager.mapper.ModelMapper;
import org.workshop.automanager.model.BrandEntity;
import org.workshop.automanager.model.ModelEntity;
import org.workshop.automanager.repository.ModelRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModelServiceTest {
    @Mock
    private ModelRepository modelRepository;

    @Mock
    private BrandService brandService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ModelService modelService;

    @Test
    void createWhenModelAlreadyExistsTest() {
        //Arrange
        ModelRequestDTO request = new ModelRequestDTO("Chevrolet", 1);
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setId(1);
        brandEntity.setName("Teste");

        //Act
        when(brandService.getBrandEntityById(request.getBrandId())).thenReturn(brandEntity);
        when(modelRepository.existsByNameAndBrand(request.getName(), brandEntity)).thenReturn(true);

        //Assert
        AlreadyExistsException exception = Assertions.assertThrows(AlreadyExistsException.class, () ->
                modelService.create(request));
        Assertions.assertEquals("O modelo Chevrolet da marca Teste já existe.", exception.getMessage());
        verify(modelRepository, never()).save(any());
    }

    @Test
    void createSuccessfully() {
        //Arrange
        ModelRequestDTO request = new ModelRequestDTO("Chevrolet", 1);
        ModelEntity entity = new ModelEntity();
        entity.setName("Chevrolet");

        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setId(1);
        brandEntity.setName("Chevrolet");
        entity.setBrand(brandEntity);

        //Act
        when(brandService.getBrandEntityById(request.getBrandId())).thenReturn(brandEntity);
        when(modelRepository.existsByNameAndBrand(request.getName(), brandEntity)).thenReturn(false);
        when(modelMapper.toModelEntity(request)).thenReturn(entity);
        modelService.create(request);

        //Assert
        verify(modelRepository, times(1)).save(entity);
    }

    @Test
    void getByIdSuccessfully() {
        int id = 1;

        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setName("Fiat");

        ModelEntity entity = new ModelEntity();
        entity.setName("Palio");
        entity.setBrand(brandEntity);

        ModelResponseDTO expectedResponse = new ModelResponseDTO(id, entity.getName(), "Fiat");

        when(modelRepository.findById(id)).thenReturn(Optional.of(entity));
        when(modelMapper.toModelResponseDTO(entity)).thenReturn(expectedResponse);
        ModelResponseDTO result = modelService.getById(id);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedResponse, result);
        verify(modelMapper, times(1)).toModelResponseDTO(entity);
        verify(modelRepository, times(1)).findById(id);
    }

    @Test
    void getByIdNotFound() {
        int id = 1;

        when(modelRepository.findById(id)).thenReturn(Optional.empty());

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> modelService.getById(id));

        Assertions.assertEquals("Modelo com ID " + id + " não encontrado", exception.getMessage());
        verifyNoInteractions(modelMapper);
    }


    @ParameterizedTest //Faz o teste rodar varias vezes, cada uma com um parametro
    @NullSource //Adiciona o parametro nulo (id = null)
    @ValueSource(ints = {0, -1})
        //Lista de valores a ser testado
    void getByIdInvalidArgument(Integer id) {

        InvalidArgumentException exception = Assertions.assertThrows(InvalidArgumentException.class, () -> modelService.getById(id));

        Assertions.assertEquals("O ID " + id + " recebido é inválido", exception.getMessage());
        verifyNoInteractions(modelRepository, modelMapper);
    }

    @Test
    void getAllSuccessfully() {
        List<ModelEntity> entityList = new ArrayList<>();
        entityList.add(mock(ModelEntity.class));
        entityList.add(mock(ModelEntity.class));

        List<ModelResponseDTO> expectedList = new ArrayList<>();
        expectedList.add(new ModelResponseDTO(1, "Palio", "Fiat"));
        expectedList.add(new ModelResponseDTO(2, "Celta", "Chevrolet"));

        when(modelRepository.findAll()).thenReturn(entityList);
        when(modelMapper.toModelResponseDTOList(entityList)).thenReturn(expectedList);

        List<ModelResponseDTO> result = modelService.getAll();

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(expectedList, result);
        verify(modelRepository, times(1)).findAll();
        verify(modelMapper, times(1)).toModelResponseDTOList(entityList);
    }

    @Test
    void getAllEmptyList() {
        when(modelRepository.findAll()).thenReturn(List.of());
        when(modelMapper.toModelResponseDTOList(List.of())).thenReturn(List.of());

        List<ModelResponseDTO> result = modelService.getAll();

        Assertions.assertTrue(result.isEmpty());
        verify(modelRepository, atLeastOnce()).findAll();
        verify(modelMapper, atLeastOnce()).toModelResponseDTOList(List.of());
    }

    @Test
    void deleteByIdSuccessfully() {
        int id = 1;

        ModelEntity entity = new ModelEntity();
        entity.setName("Chevette");

        when(modelRepository.findById(id)).thenReturn(Optional.of(entity));
        modelService.deleteById(id);

        verify(modelRepository, times(1)).delete(entity);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {0, -1})
    void deleteByIdInvalidArgument(Integer id) {
        InvalidArgumentException exception = Assertions.assertThrows(InvalidArgumentException.class, () -> modelService.deleteById(id));

        Assertions.assertEquals("O ID " + id + " recebido é inválido", exception.getMessage());
        verifyNoInteractions(modelRepository);
    }

    @Test
    void deleteByIdNotFound() {
        int id = 999;
        when(modelRepository.findById(id)).thenReturn(Optional.empty());

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> modelService.deleteById(id));

        Assertions.assertEquals("O modelo nao foi encontrado.", exception.getMessage());
        verify(modelRepository, never()).delete(any(ModelEntity.class));
    }

    @Test
    void updateSuccessfully() {
        int id = 999;
        ModelRequestDTO request = new ModelRequestDTO("Chevette", 1);
        ModelEntity entity = new ModelEntity();
        entity.setName("Chevy 500");

        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setId(1);
        brandEntity.setName("GM");

        when(modelRepository.findById(id)).thenReturn(Optional.of(entity));
        when(brandService.getBrandEntityById(request.getBrandId())).thenReturn(brandEntity);
        modelService.update(request, id);

        verify(modelRepository, times(1)).save(entity);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {0, -1})
    void updateInvalidArguments(Integer id) {
        ModelRequestDTO request = new ModelRequestDTO("Chevete", 1);
        InvalidArgumentException exception = Assertions.assertThrows(InvalidArgumentException.class, () -> modelService.update(request, id));

        Assertions.assertEquals("O ID " + id + " recebido é inválido", exception.getMessage());
        verifyNoInteractions(modelRepository);
    }

    @Test
    void updateNotFound() {
        int id = 999;
        ModelRequestDTO request = new ModelRequestDTO("Chevete", 1);

        when(modelRepository.findById(id)).thenReturn(Optional.empty());

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> modelService.update(request, id));

        Assertions.assertEquals("O modelo não foi encontrado.", exception.getMessage());
        verify(brandService, never()).getBrandEntityById(any());
        verify(modelRepository, never()).save(any(ModelEntity.class));
    }

    @Test
    void updateBrandNotFound() {
        int modelId = 1;
        int brandId = 2;
        ModelRequestDTO request = new ModelRequestDTO("Novo Nome", brandId);
        ModelEntity modelEntity = new ModelEntity(); // Simula o modelo existente

        when(modelRepository.findById(modelId)).thenReturn(Optional.of(modelEntity));
        when(brandService.getBrandEntityById(brandId)).thenThrow(new NotFoundException("Marca com ID " + brandId + " não encontrada"));

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> modelService.update(request, modelId));

        Assertions.assertEquals("Marca com ID " + brandId + " não encontrada", exception.getMessage());
        verify(modelRepository, never()).save(any(ModelEntity.class));
    }
}
