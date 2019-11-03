package com.j24.security.template.controller;

import com.j24.security.template.model.Comment;
import com.j24.security.template.model.Participation;
import com.j24.security.template.service.AccountService;
import com.j24.security.template.service.CommentService;
import com.j24.security.template.service.EventService;
import com.j24.security.template.service.ParticipationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping(path = "/comment/")
public class CommentController {

    @Autowired
    private EventService eventService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ParticipationService participationService;
    @Autowired
    private CommentService commentService;


    @GetMapping("/{id}")
    public String leftComment(@PathVariable("id") Long eventId, Model model, Principal principal, HttpServletRequest request){
        Optional<Participation> optionalParticipation = participationService.findActiveByEventIdAndUserName(eventId, principal.getName());
        if (optionalParticipation.isPresent()){
            Comment comment = new Comment();
            comment.setParticipation(optionalParticipation.get());
            model.addAttribute("event_id", eventId);
            model.addAttribute("comment_to_edit", comment);
            return "comment-form";
        }
        return "redirect:" + request.getHeader("referer");
    }

    @PostMapping("/{id}")
    public String saveComment(Comment comment, @PathVariable("id") Long eventId, Principal principal){
        commentService.save(comment, principal.getName());
        return "redirect:/event/details/" + eventId;
    }

    @GetMapping("/{id}/edit/{commentId}")
    public String editComment(@PathVariable("id")Long eventId, @PathVariable("commentId") Long commentId, Principal principal, Model model) {
        Optional<Comment> optionalComment = commentService.findById(commentId);
        if (optionalComment.isPresent()){
            Comment comment = optionalComment.get();
            if (comment.getParticipation().getAccount().getUsername().equals(principal.getName())){
                model.addAttribute("event_id", eventId);
                model.addAttribute("comment_to_edit", comment);
                return "comment-form";
            }
        }
        return "redirect:/event/details/" + eventId;

    }

    @GetMapping("/{id}/delete/{commentId}")
    public String deleteComment(@PathVariable("id")Long eventId, @PathVariable("commentId") Long commentId, Principal principal){
        commentService.delete(commentId, eventId, principal.getName());
        return "redirect:/event/details/" + eventId;

    }


}
