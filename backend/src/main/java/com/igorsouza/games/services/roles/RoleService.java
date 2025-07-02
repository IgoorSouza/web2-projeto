package com.igorsouza.games.services.roles;

import com.igorsouza.games.exceptions.NotFoundException;
import com.igorsouza.games.models.Role;

import java.util.List;

public interface RoleService {
    Role getRoleByName(String roleName) throws NotFoundException;
    void createRoles(List<String> roleNames);
}
