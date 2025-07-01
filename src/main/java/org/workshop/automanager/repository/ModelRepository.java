package org.workshop.automanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.workshop.automanager.model.ModelEntity;

import org.workshop.automanager.model.BrandEntity;

public interface ModelRepository extends JpaRepository<ModelEntity, Integer> {
    Boolean existsByNameAndBrand(String name, BrandEntity brand);
}