package com.jessy.technonews.repository;

import java.util.Optional;

import com.jessy.technonews.domain.Role;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
    boolean existsRoleByName(String name);
}
