package com.ritualfresh.auth.controller;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SolicitudRecuperacionContrasenaApiRequest {
    @NotBlank
    @Email
    private String mail;
}
