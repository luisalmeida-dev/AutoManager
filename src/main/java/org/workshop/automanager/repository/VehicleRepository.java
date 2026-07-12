package org.workshop.automanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.workshop.automanager.model.VehicleEntity;

public interface VehicleRepository extends JpaRepository<VehicleEntity, Integer> {
}
