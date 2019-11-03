package com.j24.security.template.controller;

import com.j24.security.template.model.*;
import com.j24.security.template.service.AccountService;
import com.j24.security.template.service.EventService;
import com.j24.security.template.service.ParticipationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.jws.WebParam;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(path = "/event/")
public class EventController {

    @Autowired
    private EventService eventService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ParticipationService participationService;

    @GetMapping("/list")
    public String activePublicEventsList(Model model, Principal principal){
        model.addAttribute("event_name", "");
        List<Event> events = eventService.getPublicActiveEvents();
        model.addAttribute("events", events);
        model.addAttribute("active_user", principal.getName());
        return "event-list";
    }

    @GetMapping("/list/past")
    public String pastPublicEventsList(Model model, Principal principal){
        model.addAttribute("event_name", "");
        List<Event> events = eventService.getPublicPastEvents();
        model.addAttribute("events", events);
        model.addAttribute("active_user", principal.getName());
        return "event-list";
    }

    @GetMapping("/add")
    public String addEventForm(Model model, Principal principal){
        Optional<Account> accountOptional = accountService.getByUsername(principal.getName());
        List<GroupUnit> myGroupManagement = accountService.getMyGroupManagementToEvent(principal.getName());
        if (accountOptional.isPresent()){
            Event event = new Event();
            event.setEventOrganizer(accountOptional.get());
            model.addAttribute("event_to_edit", event);
            model.addAttribute("group_mangment", myGroupManagement);
            return "event-form";
        }
        return "redirect:/login";
    }

    @PostMapping("/add")
    public String addEvent(Model model, @Valid Event event, Long groupId, BindingResult bindingResult, Principal principal){
        List<GroupUnit> myGroupManagement = accountService.getMyGroupManagementToEvent(principal.getName());
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", bindingResult.getFieldError().getDefaultMessage());
            model.addAttribute("event_to_edit", event);
            model.addAttribute("group_mangment", myGroupManagement);
            return "event-form";
        }
        if (!event.isEndAfterStart()) {
            model.addAttribute("errorMessage", "Event end date must be AFTER start date");
            model.addAttribute("event_to_edit", event);
            model.addAttribute("group_mangment", myGroupManagement);
            return "event-form";
        }
        eventService.save(event, groupId, principal.getName());
        return "redirect:/event/list/my";
    }

    @GetMapping("/list/my")
    public String myEvents(Model model, Principal principal){
        List<Event> myEvents = eventService.getAllByOrganizer(principal.getName());
        model.addAttribute("events", myEvents);
        return "myevent-list";
    }

    @GetMapping("/participant/{id}")
    public String getParticipants(@PathVariable("id") Long eventId, Model model, Principal principal, HttpServletRequest request){
        Optional<Event> optionalEvent = eventService.getById(eventId);
        if (optionalEvent.isPresent()){
            Event event = optionalEvent.get();
            model.addAttribute("event", event);
            model.addAttribute("participants", event.getParticipants());
            model.addAttribute("active_user", principal.getName());
            return "event-participants-list";
        }
        return "redirect:" + request.getHeader("referer");

    }

    @GetMapping("/edit/{id}")
    private String edit(Model model, @PathVariable("id") Long eventId, Principal principal){
        Optional<Event> optionalEvent = eventService.getById(eventId);
        List<GroupUnit> myGroupManagement = accountService.getMyGroupManagementToEvent(principal.getName());
        if (optionalEvent.isPresent()){
            Event event = optionalEvent.get();
            if (event.getEventOrganizer().getUsername().equals(principal.getName())) {
                model.addAttribute("event_to_edit", event);
                model.addAttribute("group_mangment", myGroupManagement);
                return "event-form";
            }
        }
        return "redirect:/event/list/my";
    }


    @GetMapping("/join/{id}")
    public String join(@PathVariable("id") Long eventId, Principal principal, HttpServletRequest request){
        participationService.save(eventId, principal.getName());
        //return "redirect:/event/list";
        return "redirect:" + request.getHeader("referer");
    }


    @GetMapping("/list/joins")
    public String myEventsList(Model model, Principal principal){
        List<Event> myEvents = accountService.getUserActualEventsList(principal.getName());
        model.addAttribute("events", myEvents);
        model.addAttribute("active_user", principal.getName());
        return "event-list";
    }

    @GetMapping("/details/{id}")
    public String eventDetails(@PathVariable("id") Long eventId, Model model, Principal principal, HttpServletRequest request){
        Optional<Event> optionalEvent = eventService.getById(eventId);
        if (optionalEvent.isPresent()){
            model.addAttribute("event", optionalEvent.get());
            model.addAttribute("active_user", principal.getName());
            return "event-details";
        }
        return "redirect:" + request.getHeader("referer");

    }


    @PostMapping("/list/search")
    public String searchEvent(String ename, Model model, Principal principal, HttpServletRequest request){
        model.addAttribute("event_name", ename);
        boolean active = !request.getHeader("referer").contains("past");
        List<Event> events = eventService.findAllByName(active, ename);
        model.addAttribute("events", events);
        model.addAttribute("active_user", principal.getName());
        return "event-list";
    }

    @GetMapping("/delete/{id}")
    public String deleteEvent(@PathVariable("id") Long eventId, Principal principal){
        eventService.delete(eventId, principal.getName());
        return "redirect:/event/list/my";
    }




}
