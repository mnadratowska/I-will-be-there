package com.j24.security.template.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.ManyToAny;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDate;

    @CreationTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime additionTime;

    private String placeName;
    private String placeAdress;

    @Column(columnDefinition = "VARCHAR(1000)")
    private String description;
    private boolean privateEvent;
    private boolean locked;
    @Formula(value = "datediff(end_date, now())>=0")
    private boolean active;

    @ManyToOne
    private Account eventOrganizer;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "event", orphanRemoval = true)
    private Set<Participation> participations;

    @ManyToOne
    private GroupUnit group;


    public boolean isEndAfterStart(){
        return endDate.isAfter(startDate);
    }

    public boolean hasParticipation(String username){
        return participations.stream().
                anyMatch(participation -> participation.getAccount().getUsername().equals(username) && participation.isUserAcceptance());
    }

    public boolean hasInvitation(String username){
        return participations.stream()
                .anyMatch(participation -> participation.getAccount().getUsername().equals(username));
    }

    public long numberOfParticipants(){
        return participations.stream().
                filter(participation -> participation.hasUserAndOrganizerAcceptance()).count();
    }

    public List<Comment> getComments(){
        List<Comment> commentsList = new ArrayList<>();
        for (Participation participation : participations) {
            commentsList.addAll(participation.getComments());
        }
        return commentsList;
    }

    public boolean isHappeningNow(){
       return startDate.isBefore(LocalDateTime.now()) && endDate.isAfter(LocalDateTime.now());
    }

    public List<Participation> getParticipants(){
        return participations.stream()
                .filter(participation -> participation.hasUserAndOrganizerAcceptance())
                .collect(Collectors.toList());
    }
}
