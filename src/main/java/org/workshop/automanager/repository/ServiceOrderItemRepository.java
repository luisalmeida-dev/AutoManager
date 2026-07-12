package org.workshop.automanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.workshop.automanager.model.ServiceOrderItemEntity;

public interface ServiceOrderItemRepository extends JpaRepository<ServiceOrderItemEntity, Integer> {
}
