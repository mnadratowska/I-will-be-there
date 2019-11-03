package com.j24.security.template.controller;

import com.j24.security.template.model.*;
import com.j24.security.template.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.*;

@Controller
@RequestMapping(path = "/invitation/")
public class InviteController {

    @Autowired
    private ParticipationService participationService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private EventService eventService;

    @Autowired
    private GroupMembershipService groupMembershipService;

    @Autowired
    private GroupService groupService;

    @GetMapping("/event")
    public String invitationsList(Model model, Principal principal){
        List<Participation> invitations = participationService.invitations(principal.getName());
        model.addAttribute("invitations", invitations);
        model.addAttribute("active_user", principal.getName());
        return "invitation-list";
    }

    @GetMapping("/invite/event/{id}")
    public String inviteForEvent(@PathVariable("id") Long eventId, Model model, Principal principal, HttpServletRequest request){
        List<Account> allAccounts = accountService.getAll();
        Optional<Event> optionalEvent = eventService.getById(eventId);
        if (optionalEvent.isPresent()){
            Event event = optionalEvent.get();
            if (event.getEventOrganizer().getUsername().equals(principal.getName())) {
                model.addAttribute("event", event);
                model.addAttribute("accounts", allAccounts);
                return "invite-form";
            }
        }
        return "redirect:" +request.getHeader("referer");

    }

    @GetMapping("/invite/event")
    public String makeInvitation(@RequestParam("id") Long eventId, @RequestParam("userId") Long userId, Principal principal, HttpServletRequest request){
        participationService.invite(userId, eventId, principal.getName());
        return "redirect:/invitation/invite/event/" + eventId;
    }

    @GetMapping("/cancel/event")
    public String cancelEventInvitation(@RequestParam("id") Long eventId, @RequestParam("username") String username,
                                        Principal principal, HttpServletRequest request){
        participationService.cancel(eventId, username, principal.getName());
        //return "redirect:" + request.getHeader("referer");
        return "redirect:/invitation/invite/event/" + eventId;
    }

    @PostMapping("/invite/event/{id}/search")
    public String searchUser(@PathVariable("id") Long eventId, String username, Model model, Principal principal, HttpServletRequest request){
        Optional<Event> optionalEvent = eventService.getById(eventId);
        if (optionalEvent.isPresent()) {
            Event event = optionalEvent.get();
            if (event.getEventOrganizer().getUsername().equals(principal.getName())) {
                model.addAttribute("event", event);
                model.addAttribute("user_name", username);
                List<Account> accounts = accountService.findAllByUsername(username);
                model.addAttribute("accounts", accounts);
                return "invite-form";
            }
        }
            return "redirect:" +request.getHeader("referer");
    }

    @GetMapping("/ignore/event/{id}")
    private String deleteInvitation(@PathVariable("id") Long participationId, Principal principal, HttpServletRequest request){
        participationService.deleteByParticipationId(participationId, principal.getName());
        return "redirect:" + request.getHeader("referer");
    }


    @GetMapping("/invite/group/{id}")
    public String inviteToGroup(@PathVariable("id") Long groupId, Model model, Principal principal, HttpServletRequest request){
        List<Account> allAccounts = accountService.getAll();
        Optional<GroupUnit> optionalGroup = groupService.findById(groupId);
        if (optionalGroup.isPresent()){
            GroupUnit group = optionalGroup.get();
            if (group.getGroupManager().getUsername().equals(principal.getName())) {
                model.addAttribute("group", group);
                model.addAttribute("accounts", allAccounts);
                return "group-invite-form";
            }
        }
        return "redirect:" +request.getHeader("referer");

    }
    @PostMapping("/invite/group/{id}/search")
    public String searchUserGroup(@PathVariable("id") Long groupId, String uname, Model model, Principal principal, HttpServletRequest request){
        Optional<GroupUnit> optionalGroup = groupService.findById(groupId);
        if (optionalGroup.isPresent()) {
            GroupUnit group = optionalGroup.get();
            if (group.getGroupManager().getUsername().equals(principal.getName())) {
                model.addAttribute("group", group);
                model.addAttribute("user_name", uname);
                List<Account> accounts = accountService.findAllByUsername(uname);
                model.addAttribute("accounts", accounts);
                return "group-invite-form";
            }
        }
        return "redirect:" +request.getHeader("referer");
    }

    @GetMapping("/invite/group")
    public String inviteToGroup(@RequestParam("id") Long groupId, @RequestParam("username") String username,
                                Principal principal, HttpServletRequest request){
        groupMembershipService.invite(groupId, username, principal.getName());
        //return "redirect:" + request.getHeader("referer");
        return "redirect:/invitation/invite/group/" + groupId;
    }

    @GetMapping("/cancel/group")
    public String cancelGroupInvitation(@RequestParam("id") Long groupId, @RequestParam("username") String username,
                                Principal principal, HttpServletRequest request){
        groupMembershipService.cancel(groupId, username, principal.getName());
        //return "redirect:" + request.getHeader("referer");
        return "redirect:/invitation/invite/group/" + groupId;
    }

    @GetMapping("/group")
    public String invitationList(Model model, Principal principal){
        List<GroupMembership> invitations = groupMembershipService.invitations(principal.getName());
        model.addAttribute("invitations", invitations);
        return "group-invitation-list";
    }

    @GetMapping("/ignore/group/{id}")
    private String deleteGroupInvitation(@PathVariable("id") Long groupMembershipId, Principal principal, HttpServletRequest request) {
        groupMembershipService.deleteById(groupMembershipId, principal.getName());
        return "redirect:" + request.getHeader("referer");
    }




}
