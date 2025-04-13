package org.workshop.automanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.workshop.automanager.dto.request.CustomerRequestDTO;
import org.workshop.automanager.dto.response.CustomerResponseDTO;
import org.workshop.automanager.enums.RoleEnum;
import org.workshop.automanager.exception.AlreadyExistsException;
import org.workshop.automanager.exception.InvalidArgumentException;
import org.workshop.automanager.exception.NotFoundException;
import org.workshop.automanager.mapper.CustomerMapper;
import org.workshop.automanager.model.CustomerEntity;
import org.workshop.automanager.repository.CustomerRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerMapper customerMapper;

    public void creteCustomer(CustomerRequestDTO requestDTO) {
        if (!requestDTO.getRole().equals(RoleEnum.CUSTOMER)) {
            throw new InvalidArgumentException("O Cargo " + requestDTO.getRole() + " recebido é inválido");
        } else if (customerRepository.existsByCpf(requestDTO.getCpf())) {
            throw new AlreadyExistsException("O clienta já está registrado.");
        }
        customerRepository.save(customerMapper.toCustomerEntity(requestDTO));
    }

    public CustomerResponseDTO getCustomerByCpf(String cpf) {
        if (cpf == null) {
            throw new InvalidArgumentException("O CPF " + cpf + " recebido é inválido");
        }
        CustomerEntity entity = customerRepository.findByCpf(cpf);
        if (entity == null) {
            throw new NotFoundException("O CPF " + cpf + " não foi encontrado!");
        }
        return customerMapper.toCustomerResponseDTO(entity);
    }

    public CustomerResponseDTO getCustomerById(Integer id) {
        if (id == null || id <= 0) {
            throw new InvalidArgumentException("O ID " + id + " recebido é inválido");
        }
        Optional<CustomerEntity> entity = customerRepository.findById(id);
        if (entity.isEmpty()) {
            throw new NotFoundException("Cliente não encontrado!");
        }
        return customerMapper.toCustomerResponseDTO(entity.get());
    }

    public List<CustomerResponseDTO> getAll() {
        return customerMapper.toCustomerResponseDTOList(customerRepository.findAll());
    }

    public void updateCustomerByCpf(CustomerRequestDTO requestDTO) {
        if (requestDTO.getCpf() == null || requestDTO.getCpf().isBlank()) {
            throw new InvalidArgumentException("O CPF " + requestDTO.getCpf() + " recebido é inválido");
        }
        CustomerEntity entity = customerRepository.findByCpf(requestDTO.getCpf());
        if (entity == null) {
            throw new NotFoundException("Cliente não encontrado!");
        }
        entity.setAddress(requestDTO.getAddress());
        entity.setName(requestDTO.getName());
        entity.setPhone(requestDTO.getPhone());
        entity.setEmail(requestDTO.getEmail());
        customerRepository.save(entity);
    }

    public void deleteByCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            throw new InvalidArgumentException("O CPF " + cpf + " recebido é inválido");
        }
        CustomerEntity entity = customerRepository.findByCpf(cpf);
        if (entity == null) {
            throw new NotFoundException("Cliente não encontrado!");
        }
        customerRepository.delete(entity);
    }
}
