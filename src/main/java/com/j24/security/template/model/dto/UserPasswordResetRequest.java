package com.j24.security.template.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPasswordResetRequest {

    private Long accountId;

    @NotEmpty
    @Size(min = 6, max = 100)
    private String password;



}


