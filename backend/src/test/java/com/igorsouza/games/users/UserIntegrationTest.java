package com.igorsouza.games.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.igorsouza.games.dtos.users.ChangePassword;
import com.igorsouza.games.dtos.users.SetUserRoles;
import com.igorsouza.games.dtos.users.UpdateUser;
import com.igorsouza.games.exceptions.NotFoundException;
import com.igorsouza.games.models.Role;
import com.igorsouza.games.models.User;
import com.igorsouza.games.repositories.UserRepository;
import com.igorsouza.games.services.jwt.JwtService;
import com.igorsouza.games.services.roles.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RoleService roleService;

    private User testUser;
    private User existingUser;
    private String testToken;

    private final ObjectMapper mapper = new ObjectMapper();
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setup() throws NotFoundException {
        testUser = new User();
        testUser.setName("test");
        testUser.setEmail("test@example.com");
        testUser.setPassword("123456");
        testUser.setEmailVerified(false);

        List<Role> testUserRoles = new ArrayList<>();
        testUserRoles.add(roleService.getRoleByName("SUPER_ADMIN"));
        testUser.setRoles(testUserRoles);

        testUser = userRepository.save(testUser);

        existingUser = new User();
        existingUser.setName("existing");
        existingUser.setEmail("existing@email.com");
        existingUser.setPassword("123456");
        existingUser.setEmailVerified(false);

        List<Role> existingUserRoles = new ArrayList<>();
        existingUserRoles.add(roleService.getRoleByName("USER"));
        existingUser.setRoles(existingUserRoles);

        existingUser = userRepository.save(existingUser);

        testToken = jwtService.generateToken(testUser.getId());
    }

    @Test
    @DisplayName("Deve atualizar o nome e o email com sucesso")
    void shouldUpdateNameAndEmailSuccessfully() throws Exception {
        UpdateUser dto = new UpdateUser("Updated Name", "newemail@example.com");

        mockMvc.perform(put("/user")
                        .with(csrf())
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
        assertThat(updatedUser.getEmail()).isEqualTo("newemail@example.com");
        assertThat(updatedUser.isEmailVerified()).isFalse();
        assertThat(updatedUser.isNotificationsEnabled()).isFalse();
    }

    @Test
    @DisplayName("Deve desativar emailVerified e notificationsEnabled ao mudar o email")
    void shouldDisableEmailVerifiedAndNotificationsWhenEmailChanges() throws Exception {
        testUser.setEmailVerified(true);
        testUser.setNotificationsEnabled(true);
        userRepository.save(testUser);

        UpdateUser dto = new UpdateUser("Test User", "changedemail@example.com");

        mockMvc.perform(put("/user")
                        .with(csrf())
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();

        assertThat(updatedUser.getEmail()).isEqualTo("changedemail@example.com");
        assertThat(updatedUser.isEmailVerified()).isFalse();
        assertThat(updatedUser.isNotificationsEnabled()).isFalse();
    }

    @Test
    @DisplayName("Deve retornar 409 quando o email já existe")
    void shouldReturnConflictWhenEmailAlreadyExists() throws Exception {
        User otherUser = new User();
        otherUser.setName("Other");
        otherUser.setEmail("existing@example.com");
        otherUser.setPassword("abc");
        userRepository.save(otherUser);

        UpdateUser dto = new UpdateUser("New Name", "existing@example.com");

        mockMvc.perform(put("/user")
                        .with(csrf())
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Deve ativar as notificações com sucesso")
    void shouldEnableNotificationsSuccessfully() throws Exception {
        testUser.setEmailVerified(true);
        testUser.setNotificationsEnabled(false);
        userRepository.save(testUser);

        mockMvc.perform(put("/user/toggle-notifications")
                        .with(csrf())
                        .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Notifications successfully enabled."));

        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(updatedUser.isNotificationsEnabled()).isTrue();
    }

    @Test
    @DisplayName("Deve desativar as notificações com sucesso")
    void shouldDisableNotificationsSuccessfully() throws Exception {
        testUser.setEmailVerified(true);
        testUser.setNotificationsEnabled(true);
        userRepository.save(testUser);

        mockMvc.perform(put("/user/toggle-notifications")
                        .with(csrf())
                        .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Notifications successfully disabled."));

        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(updatedUser.isNotificationsEnabled()).isFalse();
    }

    @Test
    @DisplayName("Deve retornar 401 se o email não estiver verificado")
    void shouldReturn401WhenEmailIsNotVerified() throws Exception {
        testUser.setEmailVerified(false);
        userRepository.save(testUser);

        mockMvc.perform(put("/user/toggle-notifications")
                        .with(csrf())
                        .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("You must verify your email before enabling notifications."));
    }

    @Test
    @DisplayName("Deve alterar a senha com sucesso")
    void shouldChangePasswordSuccessfully() throws Exception {
        String oldPassword = "123456";
        String newPassword = "newPass789";

        testUser.setPassword(passwordEncoder.encode(oldPassword));
        userRepository.save(testUser);

        ChangePassword dto = new ChangePassword(oldPassword, newPassword);

        mockMvc.perform(put("/user/change-password")
                        .with(csrf())
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Password successfully changed."));

        User updated = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(passwordEncoder.matches(newPassword, updated.getPassword())).isTrue();
    }

    @Test
    @DisplayName("Deve retornar 401 se a senha atual estiver incorreta")
    void shouldReturn401WhenCurrentPasswordIsIncorrect() throws Exception {
        testUser.setPassword(passwordEncoder.encode("realPass123"));
        userRepository.save(testUser);

        ChangePassword dto = new ChangePassword("wrongPass", "newPass");

        mockMvc.perform(put("/user/change-password")
                        .with(csrf())
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Current password is incorrect."));
    }

    @Test
    @DisplayName("Deve retornar 400 se a nova senha for igual à atual")
    void shouldReturn400WhenNewPasswordEqualsCurrent() throws Exception {
        String samePassword = "samePass";

        testUser.setPassword(passwordEncoder.encode(samePassword));
        userRepository.save(testUser);

        ChangePassword dto = new ChangePassword(samePassword, samePassword);

        mockMvc.perform(put("/user/change-password")
                        .with(csrf())
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The new password cannot be the same as the current password."));
    }

    @Test
    @DisplayName("Deve atualizar roles com sucesso")
    void shouldUpdateRolesSuccessfully() throws Exception {
        SetUserRoles dto = new SetUserRoles(existingUser.getId(), List.of("ADMIN"));

        mockMvc.perform(put("/user/roles")
                        .with(csrf())
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        User updatedUser = userRepository.findById(existingUser.getId()).orElseThrow();
        assertThat(updatedUser.getRoles()).extracting(Role::getName).containsExactly("ADMIN");
    }

    @Test
    @DisplayName("Deve retornar 400 ao tentar atribuir role SUPER_ADMIN diretamente")
    void shouldReturn400WhenAssigningSuperAdminRoleDirectly() throws Exception {
        SetUserRoles dto = new SetUserRoles(existingUser.getId(), List.of("SUPER_ADMIN"));

        mockMvc.perform(put("/user/roles")
                        .with(csrf())
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Cannot assign SUPER_ADMIN role directly."));
    }

    @Test
    @DisplayName("Deve retornar 400 ao tentar alterar roles de um usuário SUPER_ADMIN")
    void shouldReturn400WhenChangingRolesOfSuperAdminUser() throws Exception {
        SetUserRoles dto = new SetUserRoles(testUser.getId(), List.of("USER"));

        mockMvc.perform(put("/user/roles")
                        .with(csrf())
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Cannot change roles of a SUPER_ADMIN user."));
    }

    @Test
    @DisplayName("Deve retornar 404 se o papel não existir")
    void shouldReturn404IfRoleNotFound() throws Exception {
        SetUserRoles dto = new SetUserRoles(existingUser.getId(), List.of("ROLE"));

        mockMvc.perform(put("/user/roles")
                        .with(csrf())
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Role with name ROLE not found."));
    }

    @Test
    @DisplayName("Deve excluir a conta com sucesso")
    void shouldDeleteUserSuccessfully() throws Exception {
        mockMvc.perform(delete("/user")
                        .with(csrf())
                        .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isOk())
                .andExpect(content().string("User successfully deleted."));

        boolean exists = userRepository.existsById(testUser.getId());
        assertThat(exists).isFalse();
    }
}
