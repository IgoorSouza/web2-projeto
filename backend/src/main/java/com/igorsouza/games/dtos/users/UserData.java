package com.igorsouza.games.dtos.users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserData {
    private UUID id;
    private String name;
    private String email;
    private boolean emailVerified;
    private List<String> roles;
}
