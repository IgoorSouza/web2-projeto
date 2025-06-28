package com.igorsouza.games.dtos.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NewUser {
    private String name;
    private String email;
    private String password;
}
