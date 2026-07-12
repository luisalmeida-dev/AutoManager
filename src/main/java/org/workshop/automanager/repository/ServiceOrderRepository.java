package org.workshop.automanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.workshop.automanager.model.ServiceOrderEntity;

public interface ServiceOrderRepository extends JpaRepository<ServiceOrderEntity, Integer> {
}
