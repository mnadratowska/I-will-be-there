package com.j24.security.template.controller;

import com.j24.security.template.model.Account;
import com.j24.security.template.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping(path = "/account/")
public class AccountController {

    @Autowired
    AccountService accountService;

    @GetMapping("/profile")
    public String accountProfile(Model model, Principal principal){
        Optional<Account> optionalAccount = accountService.getByUsername(principal.getName());
        if (optionalAccount.isPresent()){
            model.addAttribute("account", optionalAccount.get());
            return "account-details";
        }
        return "redirect:/login";
    }

    @GetMapping("/delete")
    public String deleteAccount(Principal principal){
        accountService.delete(principal.getName());
        return "redirect:/login";
    }

    @GetMapping("/edit")
    public String editAccountForm(Principal principal, Model model){
        Optional<Account> optionalAccount = accountService.getByUsername(principal.getName());
        if (optionalAccount.isPresent()){
            model.addAttribute("account_to_edit", optionalAccount.get());
            return "account-form";
        }
        return "redirect:/account/profile";
    }

    @PostMapping("/edit")
    public String editAccount(Model model, @Valid Account account, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", bindingResult.getFieldError().getDefaultMessage());
            return "account-form";
        }

        accountService.save(account);
        return "redirect:/account/profile";
    }
}
