package com.j24.security.template.service;

import com.j24.security.template.model.Account;
import com.j24.security.template.model.AccountRole;
import com.j24.security.template.model.Comment;
import com.j24.security.template.model.Event;
import com.j24.security.template.repository.AccountRepository;
import com.j24.security.template.repository.CommentRepository;
import com.j24.security.template.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private AccountRepository accountRepository;

    public void save(Comment comment, String name) {
        if (comment.getParticipation().getAccount().getUsername().equals(name)){
            commentRepository.save(comment);
        }
    }

    public void delete(Long commentId, Long eventId, String name) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        Optional<Account> optionalAccount = accountRepository.findByUsername(name);

        if (optionalEvent.isPresent() && optionalComment.isPresent() && optionalAccount.isPresent()){
            Event event = optionalEvent.get();
            Comment comment = optionalComment.get();
            Account account = optionalAccount.get();
            if (comment.getParticipation().getAccount().equals(account) || event.getEventOrganizer().equals(account) || account.isAdmin()){
                commentRepository.deleteById(commentId);
            }
        }

    }

    public Optional<Comment> findById(Long commentId) {
        return commentRepository.findById(commentId);
    }
}
