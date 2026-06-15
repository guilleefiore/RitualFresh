package com.ritualfresh.notifications.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

// Propiedades propias del modulo de correo.
// Agrupan los datos necesarios para construir enlaces y remitente.
@ConfigurationProperties(prefix = "ritualfresh.mail")
@Getter
@Setter
public class MailProperties {
    // Activa el envio real por SMTP cuando el entorno ya tiene credenciales configuradas.
    private boolean enabled;

    // Direccion visible como remitente de los correos.
    private String from = "no-reply@ritualfresh.local";

    // URL publica del backend para armar el link de validacion.
    private String backendBaseUrl = "http://localhost:8080";

    // URL publica del frontend para armar el link de recuperacion.
    private String frontendBaseUrl = "http://localhost:5173";
}
