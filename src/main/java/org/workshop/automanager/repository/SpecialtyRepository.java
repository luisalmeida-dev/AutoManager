package org.workshop.automanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.workshop.automanager.model.SpecialtyEntity;

public interface SpecialtyRepository extends JpaRepository<SpecialtyEntity, Integer> {
}
