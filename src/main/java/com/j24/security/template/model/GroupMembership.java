package com.j24.security.template.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GroupMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Account account;

    @ManyToOne
    private GroupUnit groupUnit;

    private boolean managerAcceptance;

    private boolean userAcceptance;

    public boolean hasAllAcceptance(){
        return managerAcceptance && userAcceptance;
    }

}
