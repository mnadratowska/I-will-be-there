package com.j24.security.template.model;

import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String email;

    @CreationTimestamp
    private LocalDateTime additionTime;

    private boolean locked;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @Cascade(value = {org.hibernate.annotations.CascadeType.DETACH})
    private Set<AccountRole> roles;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "eventOrganizer")
    private Set<Event> events;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "account", orphanRemoval = true)
    private Set<Participation> participations;


    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "groupManager", orphanRemoval = true)
    private Set<GroupUnit> groupManagment;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "account", orphanRemoval = true)
    private Set<GroupMembership> groupMemberships;

    public boolean isAdmin() {
        return roles.stream()
                .map(AccountRole::getName)
                .anyMatch(s -> s.equals("ADMIN"));
    }
}
