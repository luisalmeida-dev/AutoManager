package org.workshop.automanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.workshop.automanager.model.WorkshopEntity;

public interface WorkshopRepository extends JpaRepository<WorkshopEntity, Integer> {
}
