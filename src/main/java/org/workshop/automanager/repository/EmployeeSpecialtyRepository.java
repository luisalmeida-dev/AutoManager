package org.workshop.automanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.workshop.automanager.model.EmployeeSpecialtyEntity;

public interface EmployeeSpecialtyRepository extends JpaRepository<EmployeeSpecialtyEntity, Integer> {
}
