package org.workshop.automanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.workshop.automanager.model.ModelEntity;

public interface ModelRepository extends JpaRepository<ModelEntity, Integer> {
    Boolean existsByNameAndBrandId(String name, Integer brandId);
}