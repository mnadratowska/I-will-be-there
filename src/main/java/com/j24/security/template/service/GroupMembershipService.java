package com.j24.security.template.service;

import com.j24.security.template.model.Account;
import com.j24.security.template.model.GroupUnit;
import com.j24.security.template.model.GroupMembership;
import com.j24.security.template.repository.AccountRepository;
import com.j24.security.template.repository.GroupMembershipRepository;
import com.j24.security.template.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GroupMembershipService {

    @Autowired
    private GroupMembershipRepository groupMembershipRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private AccountRepository accountRepository;

    public void join(Long groupId, String name) {
        Optional<GroupUnit> optionalGroup = groupRepository.findById(groupId);
        Optional<Account> optionalAccount = accountRepository.findByUsername(name);

        GroupMembership groupMembership;

        if (optionalGroup.isPresent() && optionalAccount.isPresent()){
            GroupUnit group = optionalGroup.get();
            Account account = optionalAccount.get();
            if (groupMembershipRepository.existsByGroupUnitAndAccount(group, account)){
                groupMembership = groupMembershipRepository.getByGroupUnitAndAccount(group, account);
            } else {
                groupMembership = new GroupMembership();
                groupMembership.setGroupUnit(group);
                groupMembership.setAccount(account);
            }
            groupMembership.setUserAcceptance(true);

            groupMembershipRepository.save(groupMembership);
        }

    }

    public void invite(Long groupId, String username, String name) {
        Optional<GroupUnit> optionalGroup = groupRepository.findById(groupId);
        Optional<Account> optionalAccount = accountRepository.findByUsername(username);
        GroupMembership groupMembership;

        if (optionalGroup.isPresent() && optionalAccount.isPresent()){
            GroupUnit group = optionalGroup.get();
            Account account = optionalAccount.get();
            if (!group.getGroupManager().getUsername().equals(name)) return;

            if (groupMembershipRepository.existsByGroupUnitAndAccount(group, account)){
                groupMembership = groupMembershipRepository.getByGroupUnitAndAccount(group, account);
            } else {
                groupMembership = new GroupMembership();
                groupMembership.setGroupUnit(group);
                groupMembership.setAccount(account);
            }
            groupMembership.setManagerAcceptance(true);

            groupMembershipRepository.save(groupMembership);
        }
    }

    public void cancel(Long groupId, String username, String name) {
        Optional<GroupUnit> optionalGroup = groupRepository.findById(groupId);
        Optional<Account> optionalAccount = accountRepository.findByUsername(username);

        if (optionalAccount.isPresent() && optionalGroup.isPresent()) {
            GroupUnit group = optionalGroup.get();
            Account account = optionalAccount.get();
            if (!group.getGroupManager().getUsername().equals(name)) return;

            if (groupMembershipRepository.existsByGroupUnitAndAccount(group, account)) {
                GroupMembership groupMembership = groupMembershipRepository.getByGroupUnitAndAccount(group, account);
                groupMembershipRepository.delete(groupMembership);
            }
        }
    }

    public List<GroupMembership> invitations(String name) {
        return groupMembershipRepository.findAllByAccount_UsernameAndManagerAcceptanceIsTrueAndUserAcceptanceIsFalse(name);
    }

    public long numberOfInvitations(String name){
        return groupMembershipRepository.countAllByAccount_UsernameAndManagerAcceptanceIsTrueAndUserAcceptanceIsFalse(name);
    }

    public void deleteById(Long groupMembershipId, String name) {
        Optional<GroupMembership> optionalGroupMembership = groupMembershipRepository.findById(groupMembershipId);
        if (optionalGroupMembership.isPresent()){
            GroupMembership groupMembership = optionalGroupMembership.get();
            String groupManager = groupMembership.getGroupUnit().getGroupManager().getUsername();
            String username = groupMembership.getAccount().getUsername();
            if (username.equals(name) || groupManager.equals(name)){
                groupMembershipRepository.deleteById(groupMembershipId);
            }
        }
    }


}
