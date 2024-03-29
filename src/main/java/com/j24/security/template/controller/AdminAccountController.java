package com.j24.security.template.controller;

import com.j24.security.template.model.Account;
import com.j24.security.template.model.dto.UserPasswordResetRequest;
import com.j24.security.template.service.AccountRoleService;
import com.j24.security.template.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.management.openmbean.CompositeData;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(path = "/admin/account/")
@PreAuthorize(value = "hasRole('ADMIN')")
public class AdminAccountController {

    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountRoleService accountRoleService;

    @GetMapping("/list")
    public String getUserList(Model model) {
        List<Account> accountList = accountService.getAll();
        model.addAttribute("accounts", accountList);
        return "account-list";
    }

    @GetMapping("/remove")
    public String remove(@RequestParam("accountId") Long id) {
        accountService.removeAccount(id);

        return "redirect:/admin/account/list";
    }

    @GetMapping("/toggleLock")
    public String toggleLock(@RequestParam("accountId") Long id) {
        accountService.toggleLock(id);

        return "redirect:/admin/account/list";
    }

    @GetMapping("/editRoles")
    public String editRoles(Model model, @RequestParam("accountId") Long id) {
        Optional<Account> accountOptional = accountService.getById(id);
        if (accountOptional.isPresent()) {
            model.addAttribute("roles", accountRoleService.getAll());
            model.addAttribute("user", accountOptional.get());

            return "account-roles";
        }
        return "redirect:/admin/account/list";
    }

    @PostMapping("/editRoles")
    public String editRoles(Long accountId, HttpServletRequest request) {
        accountService.updateRoles(accountId, request);

        return "redirect:/admin/account/list";
    }

    @GetMapping("/resetPassword")
    public String resetPasswordForm(Model model, @RequestParam("accountId") Long id){
        Optional<Account> optionalAccount = accountService.getById(id);
        if (optionalAccount.isPresent()){
            model.addAttribute("user", optionalAccount.get());
            return "account-passwordreset";
        }
        return "redirect:/admin/account/list";
    }

    @PostMapping("/resetPassword")
    public String resetPassword(UserPasswordResetRequest request){
        accountService.resetPassword(request);
        return "redirect:/admin/account/list";
    }
}
