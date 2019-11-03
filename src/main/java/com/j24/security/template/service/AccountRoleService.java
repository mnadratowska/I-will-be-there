package com.j24.security.template.service;

import com.j24.security.template.model.AccountRole;
import com.j24.security.template.repository.AccountRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountRoleService {
    @Autowired
    private AccountRoleRepository accountRoleRepository;

    public List<AccountRole> getAll() {
        return accountRoleRepository.findAll();
    }
}
