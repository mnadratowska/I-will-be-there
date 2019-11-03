package com.j24.security.template.repository;

import com.j24.security.template.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

   List<Event> findAllByPrivateEventIsFalseAndActiveIsTrueOrderByStartDateAsc();

   List<Event> findAllByPrivateEventIsFalseAndActiveIsFalseOrderByEndDateDesc();

   List<Event> findAllByGroupIsNullAndActiveAndPrivateEventIsFalseAndNameIsContaining(boolean active, String eventName);

   List<Event> findAllByEventOrganizer_Username(String username);


   List<Event> findAllByGroupIsNullAndActiveIsTrueAndPrivateEventIsFalseOrderByStartDateAsc();

   List<Event> findAllByGroupIsNullAndActiveIsFalseAndPrivateEventIsFalseOrderByEndDateDesc();

   boolean existsByIdAndEventOrganizer_Username(Long id, String username);
}
