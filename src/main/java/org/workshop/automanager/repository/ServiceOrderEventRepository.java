package org.workshop.automanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.workshop.automanager.model.ServiceOrderEventEntity;

public interface ServiceOrderEventRepository extends JpaRepository<ServiceOrderEventEntity, Integer> {
}
