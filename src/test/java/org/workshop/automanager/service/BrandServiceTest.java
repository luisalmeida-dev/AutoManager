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
import org.workshop.automanager.dto.request.BrandRequestDTO;
import org.workshop.automanager.dto.response.BrandresponseDTO;
import org.workshop.automanager.exception.AlreadyExistsException;
import org.workshop.automanager.exception.InvalidArgumentException;
import org.workshop.automanager.exception.NotFoundException;
import org.workshop.automanager.mapper.BrandMapper;
import org.workshop.automanager.model.BrandEntity;
import org.workshop.automanager.repository.BrandRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {
    @Mock
    private BrandRepository brandRepository;

    @Mock
    private BrandMapper brandMapper;

    @InjectMocks
    private BrandService brandService;

    @Test
    void createBrandSuccessfully() {
        BrandRequestDTO expectedRequest = mock(BrandRequestDTO.class);
        BrandEntity entity = mock(BrandEntity.class);

        when(brandMapper.toEntity(expectedRequest)).thenReturn(entity);
        brandService.createBrand(expectedRequest);

        verify(brandMapper, times(1)).toEntity(expectedRequest);
        verify(brandRepository, times(1)).save(entity);
    }

    @Test
    void createBrandAlreadyExists() {
        BrandRequestDTO expectedRequest = mock(BrandRequestDTO.class);

        when(brandRepository.existsByName(expectedRequest.getName())).thenReturn(true);
        AlreadyExistsException exception = Assertions.assertThrows(AlreadyExistsException.class, () -> brandService.createBrand(expectedRequest));

        Assertions.assertEquals(exception.getMessage(), "A marca " + expectedRequest.getName() + " já está cadastrada.");
        verify(brandRepository, never()).save(any(BrandEntity.class));
        verify(brandMapper, never()).toEntity(any(BrandRequestDTO.class));
    }

    @Test
    void getBrandSuccessfully() {
        Integer id = 999;
        BrandEntity entity = mock(BrandEntity.class);
        BrandresponseDTO expectedResponse = mock(BrandresponseDTO.class);

        when(brandRepository.findById(id)).thenReturn(Optional.of(entity));
        when(brandMapper.toBrandResponseDTO(entity)).thenReturn(expectedResponse);
        brandService.getBrand(id);

        verify(brandRepository, times(1)).findById(id);
        verify(brandMapper, times(1)).toBrandResponseDTO(entity);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {0, -1})
    void getBrandInvalidArgument(Integer id) {
        InvalidArgumentException exception = Assertions.assertThrows(InvalidArgumentException.class, () -> brandService.getBrand(id));

        Assertions.assertEquals(exception.getMessage(), "O ID " + id + " recebido é inválido");
        verifyNoInteractions(brandRepository, brandMapper);
    }

    @Test
    void getBrandNotFound() {
        Integer id = 999;

        when(brandRepository.findById(id)).thenReturn(Optional.empty());
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> brandService.getBrand(id));

        Assertions.assertEquals("Marca não encontrada.", exception.getMessage());
        verify(brandRepository, times(1)).findById(id);
        verify(brandMapper, never()).toBrandResponseDTO(any(BrandEntity.class));
    }

    @Test
    void getAllSuccessfully() {
        List<BrandEntity> excpectedList = mock(ArrayList.class);
        List<BrandresponseDTO> expectedResponseList = mock(ArrayList.class);

        when(brandRepository.findAll()).thenReturn(excpectedList);
        when(brandMapper.toBrandResponseList(excpectedList)).thenReturn(expectedResponseList);
        brandService.getAll();

        verify(brandRepository, times(1)).findAll();
        verify(brandMapper, times(1)).toBrandResponseList(excpectedList);
    }

    @Test
    void updateSuccessfully() {
        Integer id = 999;
        BrandRequestDTO expectedRequest = mock(BrandRequestDTO.class);
        BrandEntity entity = mock(BrandEntity.class);

        when(brandRepository.findById(id)).thenReturn(Optional.of(entity));
        brandService.update(expectedRequest, id);

        verify(brandRepository, times(1)).findById(id);
        verify(brandRepository, times(1)).save(entity);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {0, -1})
    void updateInvalidArgument(Integer id) {
        BrandRequestDTO expectedRequest = mock(BrandRequestDTO.class);
        InvalidArgumentException exception = Assertions.assertThrows(InvalidArgumentException.class, () -> brandService.update(expectedRequest, id));

        Assertions.assertEquals(exception.getMessage(), "O ID " + id + " recebido é inválido");
        verifyNoInteractions(brandRepository);
    }

    @Test
    void updateNotFound() {
        Integer id = 999;
        BrandRequestDTO expectedRequest = mock(BrandRequestDTO.class);

        when(brandRepository.findById(id)).thenReturn(Optional.empty());
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> brandService.update(expectedRequest, id));

        Assertions.assertEquals("Marca não encontrada.", exception.getMessage());
        verify(brandRepository, times(1)).findById(id);
        verify(brandRepository, never()).save(any(BrandEntity.class));
    }

    @Test
    void deleteSuccessfully() {
        Integer id = 999;
        BrandEntity entity = mock(BrandEntity.class);

        when(brandRepository.findById(id)).thenReturn(Optional.of(entity));
        brandService.delete(id);

        verify(brandRepository, times(1)).findById(id);
        verify(brandRepository, times(1)).delete(entity);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {0, -1})
    void deleteInvalidArgument(Integer id) {
        InvalidArgumentException exception = Assertions.assertThrows(InvalidArgumentException.class, () -> brandService.delete(id));

        Assertions.assertEquals(exception.getMessage(), "O ID " + id + " recebido é inválido");
        verifyNoInteractions(brandRepository);
    }

    @Test
    void deleteNotFound() {
        Integer id = 999;

        when(brandRepository.findById(id)).thenReturn(Optional.empty());
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> brandService.delete(id));

        Assertions.assertEquals("Marca não encontrada.", exception.getMessage());
        verify(brandRepository, never()).delete(any(BrandEntity.class));
    }

    @Test
    void getBrandNameByIdSuccessfully() {
        Integer id = 1;
        BrandEntity entity = new BrandEntity();
        entity.setName("test");
        entity.setId(id);

        when(brandRepository.findById(id)).thenReturn(Optional.of(entity));
        String result = brandService.getBrandNameById(id);

        Assertions.assertEquals(result, entity.getName());
        verify(brandRepository, times(1)).findById(id);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {0, -1})
    void getBrandNameByIdInvalidArgument(Integer id) {
        InvalidArgumentException exception = Assertions.assertThrows(InvalidArgumentException.class, () -> brandService.getBrandNameById(id));

        Assertions.assertEquals(exception.getMessage(), "O ID " + id + " recebido é inválido");
        verifyNoInteractions(brandRepository);
    }

    @Test
    void getBrandNameByIdNotFound() {
        Integer id = 999;
        when(brandRepository.findById(id)).thenReturn(Optional.empty());

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> brandService.getBrandNameById(id));

        Assertions.assertEquals(exception.getMessage(), "Não há marca com o ID " + id + " fornecido");
        verify(brandRepository).findById(id);
    }
}
