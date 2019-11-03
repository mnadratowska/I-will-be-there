package com.j24.security.template.repository;

import com.j24.security.template.model.Account;
import com.j24.security.template.model.GroupUnit;
import com.j24.security.template.model.GroupMembership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupMembershipRepository extends JpaRepository<GroupMembership, Long> {

    boolean existsByGroupUnitAndAccount(GroupUnit group, Account account);

    GroupMembership getByGroupUnitAndAccount(GroupUnit group, Account account);

    List<GroupMembership> findAllByAccount_UsernameAndManagerAcceptanceIsTrueAndUserAcceptanceIsFalse(String name);

    long countAllByAccount_UsernameAndManagerAcceptanceIsTrueAndUserAcceptanceIsFalse(String name);
}
