package com.j24.security.template.controller;

import com.j24.security.template.model.Event;
import com.j24.security.template.model.GroupMembership;
import com.j24.security.template.model.GroupUnit;
import com.j24.security.template.model.Participation;
import com.j24.security.template.model.dto.UserRegistrationRequest;
import com.j24.security.template.service.AccountService;
import com.j24.security.template.service.EventService;
import com.j24.security.template.service.GroupMembershipService;
import com.j24.security.template.service.ParticipationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping(path = "/")
public class IndexController {

    @Autowired
    private String myName;

    @Autowired
    private AccountService accountService;

    @Autowired
    private GroupMembershipService groupMembershipService;

    @Autowired
    private ParticipationService participationService;

    @GetMapping("/")
    @PreAuthorize("isAuthenticated()")
    public String getIndexPage(Model model, Principal principal) {
        long eventInvitationsNumber = participationService.numberOfInvitations(principal.getName());
        long groupInvitationsNumber = groupMembershipService.numberOfInvitations(principal.getName());
        model.addAttribute("event_invitations_number", eventInvitationsNumber);
        model.addAttribute("group_invitations_number", groupInvitationsNumber);

        List<Event> actualEvents = accountService.getUserActualEventsList(principal.getName());
        List<Event> upcommingEvents = accountService.getUserUpcommingEventsList(principal.getName());
        List<Event> pastEvents = accountService.getUserPastEventsList(principal.getName());
        model.addAttribute("actual_now_events", actualEvents);
        model.addAttribute("upcomming_events", upcommingEvents);
        model.addAttribute("past_events", pastEvents);
        model.addAttribute("active_user", principal.getName());
        List<GroupUnit> groupMembership = accountService.getMyGroupMembership(principal.getName());
        model.addAttribute("groups_member", groupMembership);


        return "index";
    }


    @GetMapping("/login")
    public String loginForm() {
        return "login-form";
    }

    @GetMapping("/register")
    public String registrationForm() {
        return "registration-form";
    }

    @PostMapping("/register")
    public String register(Model model,
                           @Valid UserRegistrationRequest request,
                           BindingResult bindingResult) {
        if (!request.arePasswordsEqual()) {
            model.addAttribute("errorMessage", "Passwords do not match");
            return "registration-form";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", bindingResult.getFieldError().getDefaultMessage());
            return "registration-form";
        }
        if (!accountService.register(request)) {
            model.addAttribute("errorMessage", "This username is already taken.");
            return "registration-form";
        }
        return "redirect:/login";
    }
}