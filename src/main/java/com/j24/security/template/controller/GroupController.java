package com.j24.security.template.controller;

import com.j24.security.template.model.Account;
import com.j24.security.template.model.Event;
import com.j24.security.template.model.GroupMembership;
import com.j24.security.template.model.GroupUnit;
import com.j24.security.template.service.AccountService;
import com.j24.security.template.service.GroupMembershipService;
import com.j24.security.template.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping(path = "/group/")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private GroupMembershipService groupMembershipService;

    @GetMapping("/add")
    public String addGroupForm(Model model, Principal principal) {
        Optional<Account> optionalAccount = accountService.getByUsername(principal.getName());
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            GroupUnit group = new GroupUnit();
            group.setGroupManager(account);
            model.addAttribute("group_to_edit", group);
            return "group-form";
        }
        return "redirect:/login";
    }

    @PostMapping("/add")
    public String addGroup(GroupUnit group) {
        groupService.save(group);
        return "redirect:/group/list/my";
    }

    @GetMapping("/list")
    public String groupList(Model model, Principal principal) {
        List<GroupUnit> groups = groupService.getAll();
        model.addAttribute("groups", groups);
        model.addAttribute("active_user", principal.getName());
        return "group-list";
    }

    @GetMapping("/list/my")
    public String myGroupList(Model model, Principal principal) {
        List<GroupUnit> myGroups = groupService.getAllByGroupManager(principal.getName());
        model.addAttribute("groups", myGroups);
        model.addAttribute("active-user", principal.getName());
        return "mygroup-list";
    }

    @GetMapping("/edit/{id}")
    public String editGroupName(@PathVariable("id") Long groupId, Model model, Principal principal) {
        Optional<GroupUnit> optionalGroup = groupService.findById(groupId);
        if (optionalGroup.isPresent()) {
            GroupUnit group = optionalGroup.get();
            if (group.getGroupManager().getUsername().equals(principal.getName())) {
                model.addAttribute("group_to_edit", group);
                return "group-form";
            }
        }
        return "redirect:/group/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteGroup(@PathVariable("id") Long groupId, Principal principal, HttpServletRequest request) {
        groupService.delete(groupId, principal.getName());
        return "redirect:" + request.getHeader("referer");
    }

    @GetMapping("/join/{id}")
    public String joinGroup(@PathVariable("id") Long groupId, Principal principal, HttpServletRequest request) {
        groupMembershipService.join(groupId, principal.getName());
        return "redirect:" + request.getHeader("referer");
    }

    @GetMapping("/details/{id}")
    public String groupDetails(@PathVariable("id") Long groupId, Model model, Principal principal, HttpServletRequest request) {
        Optional<GroupUnit> optionalGroup = groupService.findById(groupId);
        if (optionalGroup.isPresent()) {
            model.addAttribute("group", optionalGroup.get());
            model.addAttribute("active_user", principal.getName());
            return "group-details";
        }
        return "redirect:" + request.getHeader("referer");

    }

    @GetMapping("/add/event/{id}")
    public String addEventToGroup(@PathVariable("id") Long groupId, Model model, Principal principal, HttpServletRequest request) {
        Optional<Account> optionalAccount = accountService.getByUsername(principal.getName());
        List<GroupUnit> myGroupManagement = accountService.getMyGroupManagementToEvent(principal.getName());

        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            Optional<GroupUnit> optionalGroup = groupService.findById(groupId);
            if (optionalGroup.isPresent()) {
                GroupUnit group = optionalGroup.get();
                Event event = new Event();
                event.setEventOrganizer(account);
                event.setGroup(group);
                model.addAttribute("event_to_edit", event);
                model.addAttribute("group_mangment", myGroupManagement);
                return "event-form";
            }
            return "redirect:" + request.getHeader("referer");
        }
        return "redirect:/login";
    }


    @GetMapping("/members/{id}")
    public String getGroupMembers(@PathVariable("id") Long groupId, Model model, Principal principal, HttpServletRequest request) {
        Optional<GroupUnit> optionalGroup = groupService.findById(groupId);
        if (optionalGroup.isPresent()) {
            GroupUnit group = optionalGroup.get();
            model.addAttribute("group", group);
            model.addAttribute("memberships", group.getMembers());
            model.addAttribute("active_user", principal.getName());
            return "group-members-list";
        }
        return "redirect:" + request.getHeader("referer");
    }

//    @GetMapping("/members/remove/{membershipId}")
//    public String removeMember(@PathVariable("membershipId") Long membershipId,
//                               Principal principal, HttpServletRequest request) {
//        groupMembershipService.deleteById(membershipId, principal.getName());
//        return "redirect:" + request.getHeader("referer");
//    }


}
