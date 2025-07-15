package org.workshop.automanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.workshop.automanager.model.CarEntity;

public interface CarRepository extends JpaRepository<CarEntity, Integer> {
    Boolean existsByPlate(String plate);
}