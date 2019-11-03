package com.j24.security.template.service;

import com.j24.security.template.model.Account;
import com.j24.security.template.model.Event;
import com.j24.security.template.model.Participation;
import com.j24.security.template.repository.AccountRepository;
import com.j24.security.template.repository.EventRepository;
import com.j24.security.template.repository.ParticipationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ParticipationService {

    @Autowired
    private ParticipationRepository participationRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private AccountRepository accountRepository;


    public void save(Long eventId, String name) {

        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        Optional<Account> optionalAccount = accountRepository.findByUsername(name);

        Participation participation;

        if (optionalAccount.isPresent() && optionalEvent.isPresent()) {
            Account account = optionalAccount.get();
            Event event = optionalEvent.get();
            if (event.getGroup()==null || event.getGroup().hasMembership(name)) {
                if (participationRepository.existsByAccountAndEvent(account, event)) {
                    participation = participationRepository.getByAccountAndEvent(account, event);
                    participation.setUserAcceptance(true);
                } else {
                    participation = new Participation();
                    if (!event.isPrivateEvent() || event.getEventOrganizer().getUsername().equals(name)) {
                        participation.setOrganizerAcceptance(true);
                    }
                    participation.setAccount(account);
                    participation.setEvent(event);
                    participation.setUserAcceptance(true);
                }
                participationRepository.save(participation);
            }
        }
    }

    public Optional<Participation> findByEventIdAndUsername(Long eventId, String name) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        Optional<Account> optionalAccount = accountRepository.findByUsername(name);

        if (optionalAccount.isPresent() && optionalEvent.isPresent()) {
            return participationRepository.findByAccountAndEvent(optionalAccount.get(), optionalEvent.get());
        }
        return Optional.empty();
    }

    public Optional<Participation> findActiveByEventIdAndUserName(Long eventId, String name) {
        Optional<Participation> optionalParticipation = findByEventIdAndUsername(eventId, name);
        if (optionalParticipation.isPresent()) {
            if (optionalParticipation.get().hasUserAndOrganizerAcceptance()) {
                return optionalParticipation;
            }
        }
        return Optional.empty();

    }


    public List<Participation> invitations(String name) {
        List<Participation> eventInvitations = participationRepository.findAllByAccount_UsernameAndOrganizerAcceptanceIsTrueAndUserAcceptanceIsFalse(name);
        return eventInvitations.stream()
                .filter(participation -> participation.getEvent().getGroup() == null || participation.getEvent().getGroup().hasMembership(name))
                .collect(Collectors.toList());
    }

    public long numberOfInvitations(String name){
        return invitations(name).size();
    }

    public void invite(Long userId, Long eventId, String name) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isPresent()) {
            Event event = optionalEvent.get();
            if (event.getEventOrganizer().getUsername().equals(name)) {
                Optional<Account> optional = accountRepository.findById(userId);
                if (optional.isPresent()){
                    Account account = optional.get();
                    if (!participationRepository.existsByAccountAndEvent(account, event)) {
                        Participation participation = new Participation();
                        participation.setAccount(account);
                        participation.setEvent(event);
                        participation.setOrganizerAcceptance(true);
                        participationRepository.save(participation);
                    }
                }
            }


        }
    }

    public void cancel(Long eventId, String username, String name) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        Optional<Account> optionalAccount = accountRepository.findByUsername(username);

        if (optionalAccount.isPresent() && optionalEvent.isPresent()) {
            Event event = optionalEvent.get();
            Account account = optionalAccount.get();
            if (!event.getEventOrganizer().getUsername().equals(name)) return;

            if (participationRepository.existsByAccountAndEvent(account, event)) {
                Participation participation = participationRepository.getByAccountAndEvent(account, event);
                participationRepository.delete(participation);
            }
        }

    }

    public void deleteByParticipationId(Long participationId, String name) {
        Optional<Participation> optionalParticipation = participationRepository.findById(participationId);
        if (optionalParticipation.isPresent()){
            Participation participation = optionalParticipation.get();
            if (participation.getAccount().getUsername().equals(name) || participation.getEvent().getEventOrganizer().getUsername().equals(name)){
                participationRepository.deleteById(participationId);
            }
        }

    }


}
