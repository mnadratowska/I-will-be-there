package com.j24.security.template.model;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Participation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    private Event event;

    private boolean organizerAcceptance;
    private boolean userAcceptance;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "participation", orphanRemoval = true )
    private Set<Comment> comments;

    public boolean hasUserAndOrganizerAcceptance() {
        return userAcceptance && organizerAcceptance;
    }
}
