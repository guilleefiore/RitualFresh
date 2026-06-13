package com.ritualfresh.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SolicitudRecuperacionContrasenaRequest {
    private final String mail;
}
