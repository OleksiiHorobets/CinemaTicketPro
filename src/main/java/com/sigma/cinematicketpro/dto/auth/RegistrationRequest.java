package com.sigma.cinematicketpro.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {
    @NotBlank
    @Size(max = 255)
    private String firstName;
    @NotBlank
    @Size(max = 255)
    private String lastName;
    @NotNull
    @Size(min = 3, max = 50)
    private String username;
    @NotNull
    @Email
    private String email;
    @NotNull
    @Size(min = 8, max = 255)
    private String password;
}