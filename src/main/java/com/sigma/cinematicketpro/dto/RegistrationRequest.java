package com.sigma.cinematicketpro.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("first_name")
    private String firstName;
    @NotBlank
    @Size(max = 255)
    @JsonProperty("last_name")
    private String lastName;
    @Size(min = 3, max = 50)
    @NotNull
    private String username;
    @Email
    @NotNull
    private String email;
    @Size(min = 8, max = 255)
    @NotNull
    private String password;
}
