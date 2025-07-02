package com.igorsouza.games.database.seeds;

import com.igorsouza.games.config.app.RolesConfig;
import com.igorsouza.games.services.roles.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(1)
public class RoleSeed implements CommandLineRunner {

    private final RoleService roleService;
    private final RolesConfig rolesConfig;

    @Override
    public void run(String... args) {
        roleService.createRoles(rolesConfig.getRoles());
    }
}
