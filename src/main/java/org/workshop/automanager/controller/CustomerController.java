package org.workshop.automanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.workshop.automanager.dto.request.CustomerRequestDTO;
import org.workshop.automanager.dto.response.CustomerResponseDTO;
import org.workshop.automanager.service.CustomerService;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @PostMapping
    public ResponseEntity<HttpStatus> createCustomer(@RequestBody CustomerRequestDTO requestDTO) throws Throwable {
        customerService.creteCustomer(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<CustomerResponseDTO> getCustomerByCpf(@PathVariable String cpf) throws Exception {
        return ResponseEntity.ok().body(customerService.getCustomerByCpf(cpf));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable int id) throws Exception {

        return ResponseEntity.ok().body(customerService.getCustomerById(id));
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAll() {
        return ResponseEntity.ok().body(customerService.getAll());
    }

    @PutMapping
    public ResponseEntity<HttpStatus> update(@RequestBody CustomerRequestDTO requestDTO) throws Exception {
        customerService.updateCustomerByCpf(requestDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{cpf}")
    public ResponseEntity<HttpStatus> delete(@PathVariable String cpf) throws Exception {
        customerService.deleteByCpf(cpf);
        return ResponseEntity.noContent().build();
    }
}
