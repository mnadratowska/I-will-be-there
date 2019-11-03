package com.j24.security.template.service;

import com.j24.security.template.model.Account;
import com.j24.security.template.model.GroupMembership;
import com.j24.security.template.model.GroupUnit;
import com.j24.security.template.repository.AccountRepository;
import com.j24.security.template.repository.GroupMembershipRepository;
import com.j24.security.template.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private GroupMembershipRepository groupMembershipRepository;

    public void save(GroupUnit group) {

        groupRepository.save(group);
        GroupMembership groupMembership = new GroupMembership();
        groupMembership.setGroupUnit(group);
        groupMembership.setAccount(group.getGroupManager());
        groupMembership.setManagerAcceptance(true);
        groupMembership.setUserAcceptance(true);
        groupMembershipRepository.save(groupMembership);
    }

    public List<GroupUnit> getAll() {
        return groupRepository.findAll();
    }

    public List<GroupUnit> getAllByGroupManager(String name) {
        return groupRepository.findAllByGroupManager_Username(name);
    }

    public Optional<GroupUnit> findById(Long groupId) {
        return groupRepository.findById(groupId);
    }

    public void delete(Long groupId, String name) {
        if (groupRepository.existsByIdAndGroupManager_Username(groupId, name)) {
            groupRepository.deleteById(groupId);
        }
        Optional<Account> optionalAccount = accountRepository.findByUsername(name);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            if (account.isAdmin() && groupRepository.existsById(groupId)) {
                groupRepository.deleteById(groupId);
            }
        }
    }

}
