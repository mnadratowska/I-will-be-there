package com.j24.security.template.repository;


import com.j24.security.template.model.Account;
import com.j24.security.template.model.Event;
import com.j24.security.template.model.Participation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    boolean existsByAccountAndEvent(Account account, Event event);

    Optional<Participation> findByAccountAndEvent(Account account, Event event);

    Participation getByAccountAndEvent(Account account, Event event);

    void deleteByAccountAndEvent(Account account, Event event);


    List<Participation> findAllByAccount_UsernameAndOrganizerAcceptanceIsTrueAndUserAcceptanceIsFalse(String username);


}
