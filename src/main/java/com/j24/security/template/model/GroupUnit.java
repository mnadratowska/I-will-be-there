package com.j24.security.template.model;

import lombok.*;

import javax.persistence.*;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    private Account groupManager;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "groupUnit", orphanRemoval = true)
    private Set<GroupMembership> memberships;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "group", orphanRemoval = true)
    private Set<Event> events;



    public List<GroupMembership> getMembers(){
        return memberships.stream()
                .filter(groupMembership -> groupMembership.hasAllAcceptance()).collect(Collectors.toList());

    }

    public boolean hasInvitation(String username){
        return memberships.stream()
                .anyMatch(groupMembership -> groupMembership.getAccount().getUsername().equals(username) && groupMembership.isManagerAcceptance());
    }

    public boolean hasMembership(String username){
        return memberships.stream()
                .anyMatch(groupMembership -> groupMembership.getAccount().getUsername().equals(username) && groupMembership.hasAllAcceptance());
    }

    public long numberOfMembers(){
       return memberships.stream()
                .filter(groupMembership -> groupMembership.hasAllAcceptance()).count();
    }

    public List<Event> getEventsByActive(boolean active){
        return events.stream()
                .filter(event -> event.isActive() == active).sorted((o1, o2) -> compare(o1, o2)).collect(Collectors.toList());
    }

    private static int compare(Event o1, Event o2) {
        if (o1.getStartDate().isAfter(o2.getStartDate())) {
            return 1;
        } else return -1;
    }
}
