package org.workshop.automanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.workshop.automanager.model.BrandEntity;

public interface BrandRepository extends JpaRepository<BrandEntity, Integer> {
    Boolean existsByName(String name);

    BrandEntity findByName(String name);
}
