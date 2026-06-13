package com.ritualfresh.profiles.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PerfilOperacionApiResponse {
    private final String mensaje;
    private final PerfilApiResponse perfil;
}
