package com.igorsouza.games.auth;

import com.igorsouza.games.models.User;
import com.igorsouza.games.repositories.UserRepository;
import com.igorsouza.games.services.jwt.JwtService;
import com.igorsouza.games.services.mail.MailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private MailService mailService;

    private User testUser;
    private String testToken;

    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setName("test");
        testUser.setEmail("test@example.com");
        testUser.setPassword("123456");
        testUser.setEmailVerified(false);
        testUser = userRepository.save(testUser);
        testToken = jwtService.generateToken(testUser.getId());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    public void shouldRequestVerificationWhenEmailNotVerified() throws Exception {
        mockMvc.perform(post("/auth/request-verification")
                .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isOk());

        verify(mailService, times(1)).sendVerificationMail(any(), any(), any());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    public void shouldFailRequestVerificationIfAlreadyVerified() throws Exception {
        testUser.setEmailVerified(true);
        userRepository.save(testUser);

        mockMvc.perform(post("/auth/request-verification")
                        .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isConflict())
                .andExpect(content().string("Email already verified."));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    public void shouldVerifyEmailWithValidToken() throws Exception {
        mockMvc.perform(post("/auth/verify")
                        .param("token", testToken)
                        .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isOk());

        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(updatedUser.isEmailVerified()).isTrue();
    }

    @Test
    @WithMockUser(username = "test@example.com")
    public void shouldFailVerifyEmailWithInvalidToken() throws Exception {
        mockMvc.perform(post("/auth/verify")
                        .param("token", "invalid-token")
                        .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid token."));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    public void shouldNotVerifyEmailIfAlreadyVerified() throws Exception {
        testUser.setEmailVerified(true);
        userRepository.save(testUser);

        mockMvc.perform(post("/auth/verify")
                        .param("token", testToken)
                        .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isConflict())
                .andExpect(content().string("Email already verified."));
    }
}
