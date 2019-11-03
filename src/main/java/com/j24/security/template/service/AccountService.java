package com.j24.security.template.service;

import com.j24.security.template.model.Account;
import com.j24.security.template.model.AccountRole;
import com.j24.security.template.model.Event;
import com.j24.security.template.model.GroupUnit;
import com.j24.security.template.model.dto.UserPasswordResetRequest;
import com.j24.security.template.model.dto.UserRegistrationRequest;
import com.j24.security.template.repository.AccountRepository;
import com.j24.security.template.repository.AccountRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountRoleRepository accountRoleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${default.user.roles:USER}")
    private String[] defaultUserRegisterRoles;

    private static int compare(Event o1, Event o2) {
        if (o1.getStartDate().isAfter(o2.getStartDate())) {
            return 1;
        } else return -1;
    }

    public List<Account> getAll() {
        return accountRepository.findAll();
    }

    public void removeAccount(Long id) {
        Optional<Account> accountOptional = accountRepository.findById(id);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();

            if (!account.isAdmin()) {
                accountRepository.delete(account);
            }
        }
    }

    public void toggleLock(Long id) {
        Optional<Account> accountOptional = accountRepository.findById(id);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();

            account.setLocked(!account.isLocked());

            accountRepository.save(account);
        }
    }

    public boolean register(UserRegistrationRequest request) {
        if (accountRepository.existsByUsername(request.getUsername())) {
            return false;
        }

        Account account = new Account();
        account.setEmail(request.getEmail());
        account.setUsername(request.getUsername());
        account.setPassword(passwordEncoder.encode(request.getPassword()));

        account.setRoles(findRolesByName(defaultUserRegisterRoles));

        accountRepository.save(account);

        return true;
    }

    public Set<AccountRole> findRolesByName(String... roles) {
        Set<AccountRole> accountRoles = new HashSet<>();
        for (String role : roles) {
            accountRoleRepository.findByName(role).ifPresent(accountRoles::add);
        }
        return accountRoles;
    }

    public Optional<Account> getById(Long id) {
        return accountRepository.findById(id);
    }

    public void updateRoles(Long accountId, HttpServletRequest request) {
        // klucz w mapie to nazwa parametru
        // wartość to tablica, gdzie 0 element to wartość pola
        // accountId -> String[] {"2", "2"}
        // ADMIN -> String[] {"on"}
        // USER -> String[] {"on"} (jeśli nie będzie zaznaczony to nie wystąpi w mapie)

        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();

            Map<String, String[]> parameterMap = request.getParameterMap();
            Set<AccountRole> accountRoles = new HashSet<>();

            for (String key : parameterMap.keySet()) {
                accountRoleRepository.findByName(key).ifPresent(accountRoles::add);
            }

            account.setRoles(accountRoles);
            accountRepository.save(account);
        }
    }

    public Optional<Account> getByUsername(String name) {
        return accountRepository.findByUsername(name);
    }

    public void delete(String name) {
        if (accountRepository.existsByUsername(name)) {
            accountRepository.deleteByUsername(name);
        }
    }

    public void save(Account account) {
            accountRepository.save(account);
    }

    public void resetPassword(UserPasswordResetRequest request) {
        Optional<Account> accountOptional = accountRepository.findById(request.getAccountId());
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            account.setPassword(passwordEncoder.encode(request.getPassword()));

            accountRepository.save(account);
        }
    }


    public List<Account> findAllByUsername(String username) {
        return accountRepository.findAllByUsernameIsContaining(username);
    }

    public List<GroupUnit> getMyGroupMembership(String name) {
        Optional<Account> optionalAccount = accountRepository.findByUsername(name);
        if (optionalAccount.isPresent()){
            Account account = optionalAccount.get();
           return account.getGroupMemberships().stream()
                    .filter(groupMembership -> groupMembership.hasAllAcceptance())
                    .map(groupMembership -> groupMembership.getGroupUnit())
            .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public List<GroupUnit> getMyGroupManagment(String name) {
        Optional<Account> optionalAccount = accountRepository.findByUsername(name);
        if (optionalAccount.isPresent()){
            Account account = optionalAccount.get();
            return new ArrayList<>(account.getGroupManagment());
        }
        return Collections.emptyList();
    }

    public List<GroupUnit> getMyGroupManagementToEvent(String name) {
        List<GroupUnit> groupUnitList = new ArrayList<>();
        GroupUnit groupUnit = new GroupUnit();
        groupUnit.setId(0L);
        groupUnit.setName("-- none --");
        groupUnitList.add(groupUnit);
        Optional<Account> optionalAccount = accountRepository.findByUsername(name);
        if (optionalAccount.isPresent()){
            Account account = optionalAccount.get();
            groupUnitList.addAll(account.getGroupManagment());
        }
        return groupUnitList;
    }

    public List<Event> getUserActualEventsList(String name) {
        Optional<Account> optionalAccount = accountRepository.findByUsername(name);
        if (optionalAccount.isPresent()){
            Account account = optionalAccount.get();
            return account.getParticipations().stream()
                    .filter(participation -> participation.hasUserAndOrganizerAcceptance())
                    .map(participation -> participation.getEvent()).filter(event -> event.isHappeningNow())
                    .sorted(AccountService::compare)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
    public List<Event> getUserPastEventsList(String name) {
        Optional<Account> optionalAccount = accountRepository.findByUsername(name);
        if (optionalAccount.isPresent()){
            Account account = optionalAccount.get();

            return account.getParticipations().stream()
                    .filter(participation -> participation.hasUserAndOrganizerAcceptance())
                    .map(participation -> participation.getEvent()).filter(event -> !event.isActive())
                    .sorted((o1, o2) -> compare(o1,o2)*(-1))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public List<Event> getUserUpcommingEventsList(String name) {
        Optional<Account> optionalAccount = accountRepository.findByUsername(name);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            return account.getParticipations().stream()
                    .filter(participation -> participation.hasUserAndOrganizerAcceptance())
                    .map(participation -> participation.getEvent()).filter(event -> !event.isHappeningNow() && event.isActive())
                    .sorted(AccountService::compare)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
