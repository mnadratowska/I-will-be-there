package com.j24.security.template.service;

import com.j24.security.template.model.Account;
import com.j24.security.template.model.Event;
import com.j24.security.template.model.GroupUnit;
import com.j24.security.template.model.Participation;
import com.j24.security.template.repository.AccountRepository;
import com.j24.security.template.repository.EventRepository;
import com.j24.security.template.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ParticipationService participationService;

    public List<Event> getPublicActiveEvents() {
        return eventRepository.findAllByGroupIsNullAndActiveIsTrueAndPrivateEventIsFalseOrderByStartDateAsc();
    }

    public List<Event> getPublicPastEvents() {
        return eventRepository.findAllByGroupIsNullAndActiveIsFalseAndPrivateEventIsFalseOrderByEndDateDesc();
    }

    public void save(Event event, Long groupId, String name) {
        Optional<GroupUnit> optionalGroupUnit= groupRepository.findById(groupId);
        if (optionalGroupUnit.isPresent()){
            GroupUnit groupUnit = optionalGroupUnit.get();
            event.setGroup(groupUnit);
        }

        if (event.getEventOrganizer().getUsername().equals(name)){
            eventRepository.save(event);
            participationService.save(event.getId(), name);

            if (optionalGroupUnit.isPresent()){
                GroupUnit groupUnit = optionalGroupUnit.get();

                List<Account> groupMembers = groupUnit.getMemberships().stream()
                        .map(groupMembership -> groupMembership.getAccount())
                        .filter(account -> !account.getUsername().equals(name)).collect(Collectors.toList());

                for (Account member : groupMembers) {
                    participationService.invite(member.getId(), event.getId(), name);
                }
            }

        }
    }

    public Optional<Event> getById(Long eventId) {
        return eventRepository.findById(eventId);
    }


    public List<Event> findAllByName(boolean active, String eventName) {
        return eventRepository.findAllByGroupIsNullAndActiveAndPrivateEventIsFalseAndNameIsContaining(active, eventName);
    }

    public List<Event> getAllByOrganizer(String name) {
        return eventRepository.findAllByEventOrganizer_Username(name);
    }

    public void delete(Long eventId, String name) {
        if (eventRepository.existsByIdAndEventOrganizer_Username(eventId, name)){
            eventRepository.deleteById(eventId);
        }
        Optional<Account> accountOptional = accountRepository.findByUsername(name);
        if (accountOptional.isPresent()){
            Account account = accountOptional.get();
            if (account.isAdmin()){
                eventRepository.deleteById(eventId);
            }
        }
    }


}
