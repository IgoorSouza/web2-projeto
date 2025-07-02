package com.igorsouza.games.dtos.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {
    private String name;
    private String email;
    private String token;
    private boolean emailVerified;
    private boolean notificationsEnabled;
    private List<String> roles;
}
