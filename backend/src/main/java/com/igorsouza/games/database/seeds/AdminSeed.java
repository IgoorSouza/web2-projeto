package com.igorsouza.games.database.seeds;

import com.igorsouza.games.config.app.AdminConfig;
import com.igorsouza.games.dtos.auth.NewUser;
import com.igorsouza.games.services.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(2)
public class AdminSeed implements CommandLineRunner {

    private final UserService userService;
    private final AdminConfig adminConfig;

    @Override
    public void run(String... args) {
        NewUser admin = new NewUser(adminConfig.getName(), adminConfig.getEmail(), adminConfig.getPassword());
        userService.createAdmin(admin);
    }
}
