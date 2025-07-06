package com.igorsouza.games.config.app;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.super-admin")
public class SuperAdminConfig {
    private String name;
    private String email;
    private String password;
}
