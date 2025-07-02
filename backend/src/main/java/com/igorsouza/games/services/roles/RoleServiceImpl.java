package com.igorsouza.games.services.roles;

import com.igorsouza.games.exceptions.NotFoundException;
import com.igorsouza.games.models.Role;
import com.igorsouza.games.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Role getRoleByName(String roleName) throws NotFoundException {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new NotFoundException("Role with name " + roleName + " not found."));
    }

    @Override
    public void createRoles(List<String> roleNames) {
        for (String roleName : roleNames) {
            boolean roleExists = roleRepository.existsByName(roleName);

            if (roleExists) {
                log.info("Role {} already exists. Skipping creation.", roleName);
            } else {
                roleRepository.save(new Role(roleName));
                log.info("Role {} criada com sucesso.", roleName);
            }
        }
    }
}
