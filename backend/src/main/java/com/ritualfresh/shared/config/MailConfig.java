package com.ritualfresh.shared.config;

import com.ritualfresh.notifications.config.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

// Habilita el binding de las propiedades propias de correo.
@Configuration
@EnableConfigurationProperties(MailProperties.class)
public class MailConfig {
}
