package com.j24.security.template.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

//DTO
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationRequest {
    @NotEmpty
    @Size(min = 4)
    private String username;

    @Email
    @NotEmpty
    private String email;

    @NotEmpty
    @Size(min = 6, max = 100)
    private String password;

    private String passwordConfirm;


    public boolean arePasswordsEqual() {
        return password.equals(passwordConfirm);
    }
}
