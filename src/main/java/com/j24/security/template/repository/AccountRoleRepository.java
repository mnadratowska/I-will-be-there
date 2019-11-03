package com.j24.security.template.repository;

import com.j24.security.template.model.AccountRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRoleRepository extends JpaRepository<AccountRole, Long> {
    boolean existsByName(String name);

    Optional<AccountRole> findByName(String role);
}
