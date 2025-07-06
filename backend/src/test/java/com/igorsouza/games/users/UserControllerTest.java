package com.igorsouza.games.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.igorsouza.games.controllers.UserController;
import com.igorsouza.games.dtos.searches.UserGameSearchDTO;
import com.igorsouza.games.dtos.users.ChangePassword;
import com.igorsouza.games.dtos.users.UpdateUser;
import com.igorsouza.games.enums.GamePlatform;
import com.igorsouza.games.exceptions.BadRequestException;
import com.igorsouza.games.exceptions.ConflictException;
import com.igorsouza.games.exceptions.NotFoundException;
import com.igorsouza.games.exceptions.UnauthorizedException;
import com.igorsouza.games.models.User;
import com.igorsouza.games.services.jwt.JwtService;
import com.igorsouza.games.services.users.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    private String testToken;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() throws NotFoundException {
        UUID mockUserId = UUID.randomUUID();
        User mockUser = new User(
                mockUserId,
                "Igor",
                "igor.castro@estudante.iftm.edu.br",
                "password123",
                false,
                false,
                List.of(),
                List.of(),
                List.of()
        );

        when(userService.getUserById(any())).thenReturn(mockUser);
        when(jwtService.validateToken(any())).thenReturn(String.valueOf(mockUserId));
        when(jwtService.generateToken(any())).thenReturn("mocked-token");
        testToken = jwtService.generateToken(mockUserId);
    }

    @AfterEach
    void resetMocks() {
        reset(userService, jwtService);
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar histórico de pesquisas do usuário com sucesso")
    void shouldReturnSearchHistorySuccessfully() throws Exception {
        List<UserGameSearchDTO> mockSearches = List.of(
                new UserGameSearchDTO("Outer Wilds", GamePlatform.STEAM, new Date())
        );

        when(userService.getAuthenticatedUserSearches()).thenReturn(mockSearches);

        mockMvc.perform(get("/user/searches")
                        .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(userService, times(1)).getAuthenticatedUserSearches();
    }

    @Test
    @DisplayName("Deve retornar 401 ao tentar buscar o histórico de pesquisas sem autenticação")
    void shouldReturn401IfNotAuthenticated() throws Exception {
        mockMvc.perform(get("/user/searches"))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).getAuthenticatedUserSearches();
    }

    @Test
    @WithMockUser
    @DisplayName("Deve atualizar nome e email com sucesso")
    void shouldUpdateUserSuccessfully() throws Exception {
        UpdateUser payload = new UpdateUser("Novo Nome", "novoemail@example.com");

        mockMvc.perform(put("/user")
                        .with(csrf())
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(content().string("User successfully updated."));

        verify(userService, times(1)).updateAuthenticatedUser(any(UpdateUser.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar 409 se o email já estiver em uso por outro usuário")
    void shouldReturn409WhenEmailIsInUse() throws Exception {
        UpdateUser payload = new UpdateUser("Nome", "duplicado@example.com");

        doThrow(new ConflictException("User already exists."))
                .when(userService).updateAuthenticatedUser(any(UpdateUser.class));

        mockMvc.perform(put("/user")
                        .with(csrf())
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isConflict())
                .andExpect(content().string("User already exists."));
    }

    @Test
    @DisplayName("Deve retornar 401 ao tentar atualizar usuário sem autenticação")
    void shouldReturn401WhenUpdateUserWithoutAuth() throws Exception {
        UpdateUser payload = new UpdateUser("Nome", "email@example.com");

        mockMvc.perform(put("/user")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve ativar as notificações com sucesso")
    void shouldEnableNotificationsSuccessfully() throws Exception {
        when(userService.toggleNotifications()).thenReturn(true);

        mockMvc.perform(put("/user/toggle-notifications")
                        .with(csrf())
                        .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Notifications successfully enabled."));

        verify(userService, times(1)).toggleNotifications();
    }

    @Test
    @WithMockUser
    @DisplayName("Deve desativar as notificações com sucesso")
    void shouldDisableNotificationsSuccessfully() throws Exception {
        when(userService.toggleNotifications()).thenReturn(false);

        mockMvc.perform(put("/user/toggle-notifications")
                        .with(csrf())
                        .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Notifications successfully disabled."));

        verify(userService, times(1)).toggleNotifications();
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar 401 se o email não estiver verificado")
    void shouldReturn401WhenEmailIsNotVerified() throws Exception {
        doThrow(new UnauthorizedException("You must verify your email before enabling notifications."))
                .when(userService).toggleNotifications();

        mockMvc.perform(put("/user/toggle-notifications")
                        .with(csrf())
                        .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("You must verify your email before enabling notifications."));
    }

    @Test
    @DisplayName("Deve retornar 401 ao tentar ativar/desativar notificações sem autenticação")
    void shouldReturn401WhenToggleNotificationsWithoutAuth() throws Exception {
        mockMvc.perform(put("/user/toggle-notifications")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve alterar a senha com sucesso")
    void shouldChangePasswordSuccessfully() throws Exception {
        ChangePassword payload = new ChangePassword("oldPass123", "newPass456");

        mockMvc.perform(put("/user/change-password")
                        .with(csrf())
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(content().string("Password successfully changed."));

        verify(userService, times(1)).changeUserPassword(any(ChangePassword.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar 401 se a senha atual estiver incorreta")
    void shouldReturn401WhenCurrentPasswordIsWrong() throws Exception {
        ChangePassword payload = new ChangePassword("wrongPass", "newPass");

        doThrow(new UnauthorizedException("Current password is incorrect."))
                .when(userService).changeUserPassword(any());

        mockMvc.perform(put("/user/change-password")
                        .with(csrf())
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Current password is incorrect."));
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar 400 se a nova senha for igual à antiga")
    void shouldReturn400WhenNewPasswordIsSameAsOld() throws Exception {
        ChangePassword payload = new ChangePassword("samePass", "samePass");

        doThrow(new BadRequestException("The new password cannot be the same as the current password."))
                .when(userService).changeUserPassword(any());

        mockMvc.perform(put("/user/change-password")
                        .with(csrf())
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The new password cannot be the same as the current password."));
    }

    @Test
    @DisplayName("Deve retornar 401 ao tentar alterar senha sem autenticação")
    void shouldReturn401WhenChangePasswordWithoutAuth() throws Exception {
        ChangePassword payload = new ChangePassword("oldPass", "newPass");

        mockMvc.perform(put("/user/change-password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve excluir a conta do usuário com sucesso")
    void shouldDeleteUserSuccessfully() throws Exception {
        mockMvc.perform(delete("/user")
                        .with(csrf())
                        .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isOk())
                .andExpect(content().string("User successfully deleted."));

        verify(userService, times(1)).deleteAuthenticatedUser();
    }

    @Test
    @DisplayName("Deve retornar 401 ao tentar deletar usuário sem autenticação")
    void shouldReturn401WhenDeleteUserWithoutAuth() throws Exception {
        mockMvc.perform(delete("/user")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}


