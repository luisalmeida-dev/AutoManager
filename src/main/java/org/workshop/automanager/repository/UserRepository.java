package org.workshop.automanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.workshop.automanager.model.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Boolean existsByLogin(String login);
}
