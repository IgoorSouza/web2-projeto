package com.igorsouza.games.auth;

import com.igorsouza.games.controllers.AuthController;
import com.igorsouza.games.exceptions.ConflictException;
import com.igorsouza.games.exceptions.UnauthorizedException;
import com.igorsouza.games.models.User;
import com.igorsouza.games.services.auth.AuthService;
import com.igorsouza.games.services.jwt.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    private String testToken;
    private User testUser;

    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setName("test");
        testUser.setEmail("test@example.com");
        testUser.setPassword("123456");
        testUser.setEmailVerified(false);

        when(jwtService.generateToken(any())).thenReturn("mocked-token");
        testToken = jwtService.generateToken(testUser.getId());
    }

    @AfterEach
    void resetMocks() {
        reset(authService, jwtService);
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @DisplayName("POST /auth/request-verification - Sucesso")
    void shouldReturnOkWhenRequestVerificationSucceeds() throws Exception {
        when(jwtService.validateToken(any())).thenReturn(String.valueOf(testUser.getId()));

        mockMvc.perform(post("/auth/request-verification")
                        .with(csrf())
                        .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Verification email successfully sent."));

        verify(authService, times(1)).requestVerification();
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @DisplayName("POST /auth/request-verification - Email já verificado")
    void shouldReturnConflictWhenEmailAlreadyVerified() throws Exception {
        doThrow(new ConflictException("Email already verified."))
                .when(authService).requestVerification();

        mockMvc.perform(post("/auth/request-verification")
                        .with(csrf())
                        .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isConflict())
                .andExpect(content().string("Email already verified."));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @DisplayName("POST /auth/request-verification - Não autenticado")
    void shouldReturnUnauthorizedWhenUserNotAuthenticated() throws Exception {
        doThrow(new UnauthorizedException("Unauthorized"))
                .when(authService).requestVerification();

        mockMvc.perform(post("/auth/request-verification")
                        .with(csrf())
                        .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Unauthorized"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @DisplayName("POST /auth/verify?token=123 - Sucesso")
    void shouldReturnOkWhenVerificationSucceeds() throws Exception {
        mockMvc.perform(post("/auth/verify")
                        .param("token", "123")
                        .with(csrf())
                        .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Email successfully verified."));

        verify(authService, times(1)).verifyEmail("123");
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @DisplayName("POST /auth/verify?token=123 - Email já verificado")
    void shouldReturnConflictWhenAlreadyVerified() throws Exception {
        doThrow(new ConflictException("Email already verified."))
                .when(authService).verifyEmail("123");

        mockMvc.perform(post("/auth/verify")
                        .param("token", "123")
                        .with(csrf())
                        .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isConflict())
                .andExpect(content().string("Email already verified."));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @DisplayName("POST /auth/verify?token=123 - Token inválido")
    void shouldReturnUnauthorizedWhenTokenIsInvalid() throws Exception {
        doThrow(new UnauthorizedException("Invalid token."))
                .when(authService).verifyEmail("123");

        mockMvc.perform(post("/auth/verify")
                        .param("token", "123")
                        .with(csrf())
                        .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid token."));
    }
}
