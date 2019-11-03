package com.j24.security.template.repository;

import com.j24.security.template.model.Account;
import com.j24.security.template.model.GroupUnit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    void deleteByUsername(String username);

    List<Account> findAllByUsernameIsContaining(String username);


}
