package org.workshop.automanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.workshop.automanager.model.CustomerEntity;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Integer> {
    CustomerEntity findByCpf(String cpf);

    Boolean existsByCpf(String cpf);
}
