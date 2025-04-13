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
import org.workshop.automanager.dto.request.CustomerRequestDTO;
import org.workshop.automanager.dto.response.CustomerResponseDTO;
import org.workshop.automanager.enums.RoleEnum;
import org.workshop.automanager.exception.AlreadyExistsException;
import org.workshop.automanager.exception.InvalidArgumentException;
import org.workshop.automanager.exception.NotFoundException;
import org.workshop.automanager.mapper.CustomerMapper;
import org.workshop.automanager.model.CustomerEntity;
import org.workshop.automanager.repository.CustomerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService customerService;


    //TODO fazer tratamento de exceção de customer antes de fazer testes
    @Test
    void createCustomerSuccessfully() {
        CustomerRequestDTO expectedRequest = new CustomerRequestDTO("teste", "111111111111", "teste@gmail.com", "99999999999", "fercal", RoleEnum.CUSTOMER);

        when(customerMapper.toCustomerEntity(expectedRequest)).thenReturn(new CustomerEntity());

        customerService.creteCustomer(expectedRequest);

        verify(customerRepository, times(1)).save(any(CustomerEntity.class));
    }

    @Test
    void createCustomerInvalidArgument() {
        CustomerRequestDTO wrongRequest = new CustomerRequestDTO("teste", "111111111111", "teste@gmail.com", "99999999999", "fercal", RoleEnum.PAINTER);

        InvalidArgumentException exception = Assertions.assertThrows(InvalidArgumentException.class, () -> customerService.creteCustomer(wrongRequest));

        Assertions.assertEquals(exception.getMessage(), "O Cargo " + wrongRequest.getRole() + " recebido é inválido");
        verifyNoInteractions(customerMapper, customerRepository);
    }

    @Test
    void creteCustomerAlreadyExists() {
        CustomerRequestDTO expectedRequest = new CustomerRequestDTO("teste", "111111111111", "teste@gmail.com", "99999999999", "fercal", RoleEnum.CUSTOMER);

        when(customerRepository.existsByCpf(anyString())).thenReturn(true);

        AlreadyExistsException exception = Assertions.assertThrows(AlreadyExistsException.class, () -> customerService.creteCustomer(expectedRequest));

        Assertions.assertEquals(exception.getMessage(), "O clienta já está registrado.");
        verifyNoInteractions(customerMapper);
    }

    @Test
    void getCustomerByCpfSuccessfully() {
        String cpf = "999999999999";
        CustomerEntity entity = mock(CustomerEntity.class);
        CustomerResponseDTO expectedResponse = mock(CustomerResponseDTO.class);

        when(customerRepository.findByCpf(cpf)).thenReturn(entity);
        when(customerMapper.toCustomerResponseDTO(entity)).thenReturn(expectedResponse);
        customerService.getCustomerByCpf(cpf);

        verify(customerRepository, times(1)).findByCpf(cpf);
        verify(customerMapper, times(1)).toCustomerResponseDTO(entity);
    }

    @Test
    void getCustomerByCpfInvalidArgument() {
        String cpf = null;

        InvalidArgumentException exception = Assertions.assertThrows(InvalidArgumentException.class, () -> customerService.getCustomerByCpf(cpf));

        Assertions.assertEquals(exception.getMessage(), "O CPF " + cpf + " recebido é inválido");
        verifyNoInteractions(customerRepository, customerMapper);
    }

    @Test
    void getCustomerByCpfNotFound() {
        String cpf = "99999999999";
        CustomerEntity entity = mock(CustomerEntity.class);

        when(customerRepository.findByCpf(cpf)).thenReturn(null);

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> customerService.getCustomerByCpf(cpf));

        Assertions.assertEquals(exception.getMessage(), "O CPF " + cpf + " não foi encontrado!");
        verify(customerRepository, atMostOnce()).findByCpf(cpf);
        verifyNoInteractions(customerMapper);
    }

    @Test
    void getCustomerByIdSuccessfully() {
        Integer id = 999;
        CustomerEntity entity = mock(CustomerEntity.class);
        CustomerResponseDTO expectedResponse = mock(CustomerResponseDTO.class);

        when(customerRepository.findById(id)).thenReturn(Optional.of(entity));
        when(customerMapper.toCustomerResponseDTO(entity)).thenReturn(expectedResponse);
        customerService.getCustomerById(id);

        verify(customerRepository, times(1)).findById(id);
        verify(customerMapper, times(1)).toCustomerResponseDTO(entity);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {0, -1})
    void getCustomerByIdInvalidArgument(Integer id) {
        InvalidArgumentException exception = Assertions.assertThrows(InvalidArgumentException.class, () -> customerService.getCustomerById(id));

        Assertions.assertEquals(exception.getMessage(), "O ID " + id + " recebido é inválido");
        verifyNoInteractions(customerRepository, customerRepository);
    }

    @Test
    void getCustomerByIdNotFound() {
        Integer id = 999;

        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> customerService.getCustomerById(id));

        Assertions.assertEquals(exception.getMessage(), "Cliente não encontrado!");
        verify(customerRepository, times(1)).findById(id);
        verifyNoInteractions(customerMapper);
    }

    @Test
    void getAllSuccessfully() {
        List<CustomerEntity> entityList = mock(ArrayList.class);
        List<CustomerResponseDTO> expectedResponse = mock(ArrayList.class);

        when(customerRepository.findAll()).thenReturn(entityList);
        when(customerMapper.toCustomerResponseDTOList(entityList)).thenReturn(expectedResponse);
        customerService.getAll();

        verify(customerRepository, times(1)).findAll();
        verify(customerMapper, times(1)).toCustomerResponseDTOList(entityList);
    }

    @Test
    void updateCustomerByCpfSuccessfully() {
        CustomerRequestDTO request = new CustomerRequestDTO("teste", "111111111111", "teste@gmail.com", "99999999999", "fercal", RoleEnum.CUSTOMER);
        CustomerEntity entity = mock(CustomerEntity.class);

        when(customerRepository.findByCpf(request.getCpf())).thenReturn(entity);
        customerService.updateCustomerByCpf(request);

        verify(customerRepository, atLeastOnce()).save(entity);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {" ", ""})
    void updateCustomerByCpfInvalidArgument(String cpf) {
        CustomerRequestDTO expectedRequest = mock(CustomerRequestDTO.class);
        expectedRequest.setCpf(cpf);

        InvalidArgumentException exception = Assertions.assertThrows(InvalidArgumentException.class, () -> customerService.updateCustomerByCpf(expectedRequest));

        Assertions.assertEquals(exception.getMessage(), "O CPF " + expectedRequest.getCpf() + " recebido é inválido");
        verifyNoInteractions(customerRepository);
    }

    @Test
    void updateCustomerByCpfNotFound() {
        CustomerRequestDTO expectedRequest = new CustomerRequestDTO("teste", "111111111111", "teste@gmail.com", "99999999999", "fercal", RoleEnum.CUSTOMER);
        CustomerEntity entity = mock(CustomerEntity.class);

        when(customerRepository.findByCpf(expectedRequest.getCpf())).thenReturn(null);

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> customerService.updateCustomerByCpf(expectedRequest));

        Assertions.assertEquals(exception.getMessage(), "Cliente não encontrado!");
        verify(customerRepository, atMostOnce()).findByCpf(expectedRequest.getCpf());
        verify(customerRepository, never()).save(entity);
    }

    @Test
    void deleteByCpfSuccessfully() {
        String cpf = "99999999999";
        CustomerEntity entity = mock(CustomerEntity.class);

        when(customerRepository.findByCpf(cpf)).thenReturn(entity);
        customerService.deleteByCpf(cpf);

        verify(customerRepository, times(1)).findByCpf(cpf);
        verify(customerRepository, times(1)).delete(entity);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {" ", ""})
    void deleteByCpfInvalidArgument(String cpf) {
        InvalidArgumentException exception = Assertions.assertThrows(InvalidArgumentException.class, () -> customerService.deleteByCpf(cpf));

        Assertions.assertEquals(exception.getMessage(), "O CPF " + cpf + " recebido é inválido");
        verifyNoInteractions(customerRepository);
    }

    @Test
    void deleteByCpfNotFound() {
        String cpf = "99999999999";
        CustomerEntity entity = mock(CustomerEntity.class);
        when(customerRepository.findByCpf(cpf)).thenReturn(null);

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> customerService.deleteByCpf(cpf));

        Assertions.assertEquals(exception.getMessage(), "Cliente não encontrado!");
        verify(customerRepository, never()).delete(entity);
    }
}