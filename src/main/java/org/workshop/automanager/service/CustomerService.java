package org.workshop.automanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.workshop.automanager.dto.request.CustomerRequestDTO;
import org.workshop.automanager.dto.response.CustomerResponseDTO;
import org.workshop.automanager.enums.RoleEnum;
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

    public void creteCustomer(CustomerRequestDTO requestDTO) throws Exception {
        if (customerRepository.existsByCpf(requestDTO.getCpf()) || !requestDTO.getRole().equals(RoleEnum.CUSTOMER)) {
            throw new Exception("Customer already registered!");
        }
        customerRepository.save(customerMapper.toCustomerEntity(requestDTO));
    }

    public CustomerResponseDTO getCustomerByCpf(String cpf) throws Exception {
        CustomerEntity entity = customerRepository.findByCpf(cpf);
        if (entity == null) {
            throw new Exception("Customer not found!");
        }
        return customerMapper.toCustomerResponseDTO(entity);
    }

    public CustomerResponseDTO getCustomerById(int id) throws Exception {
        Optional<CustomerEntity> entity = customerRepository.findById(id);
        if (entity.isEmpty()) {
            throw new Exception("Customer not found!");
        }
        return customerMapper.toCustomerResponseDTO(entity.get());
    }

    public List<CustomerResponseDTO> getAll() {
        return customerMapper.toCustomerResponseDTOList(customerRepository.findAll());
    }

    public void updateCustomerByCpf(CustomerRequestDTO requestDTO) throws Exception {
        CustomerEntity entity = customerRepository.findByCpf(requestDTO.getCpf());
        if (entity == null) {
            throw new Exception("Customer not found!");
        }
        entity.setAddress(requestDTO.getAddress());
        entity.setName(requestDTO.getName());
        entity.setPhone(requestDTO.getPhone());
        entity.setEmail(requestDTO.getEmail());
        customerRepository.save(entity);
    }

    public void deleteByCpf(String cpf) throws Exception {
        CustomerEntity entity = customerRepository.findByCpf(cpf);
        if (entity == null) {
            throw new Exception("Customer not found!");
        }
        customerRepository.delete(entity);
    }
}
