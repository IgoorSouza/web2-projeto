package com.igorsouza.games.games;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.igorsouza.games.config.security.SecurityConfig;
import com.igorsouza.games.controllers.GamesController;
import com.igorsouza.games.dtos.games.*;
import com.igorsouza.games.enums.GamePlatform;
import com.igorsouza.games.exceptions.ConflictException;
import com.igorsouza.games.exceptions.NotFoundException;
import com.igorsouza.games.models.Role;
import com.igorsouza.games.models.User;
import com.igorsouza.games.services.games.GameService;
import com.igorsouza.games.services.jwt.JwtService;
import com.igorsouza.games.services.users.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GamesController.class)
@Import(SecurityConfig.class)
public class GamesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GameService gameService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserService userService;

    private String testToken;
    private User testUser;

    @BeforeEach
    void setup() throws NotFoundException  {
        UUID testUserId = UUID.randomUUID();
        testUser = new User(
                testUserId,
                "Igor",
                "igor.castro@estudante.iftm.edu.br",
                "password123",
                false,
                false,
                List.of(),
                List.of(),
                List.of(new Role(UUID.randomUUID(), "ADMIN"))
        );

        when(userService.getUserById(testUserId)).thenReturn(testUser);
        when(jwtService.validateToken(any())).thenReturn(String.valueOf(testUserId));
        when(jwtService.generateToken(any())).thenReturn("mocked-token");
        testToken = jwtService.generateToken(testUser.getId());
    }

    @AfterEach
    void resetMocks() {
        reset(userService, jwtService);
    }

    @Test
    @WithMockUser
    @DisplayName("GET /games/steam - sucesso")
    void shouldGetSteamGamesSuccessfully() throws Exception {
        when(gameService.getSteamGames("Doom")).thenReturn(List.of());

        mockMvc.perform(get("/games/steam")
                        .with(csrf())
                        .header("Authorization", "Bearer " + testToken)
                        .param("gameName", "Doom"))
                .andExpect(status().isOk());

        verify(gameService, times(1)).getSteamGames("Doom");
    }

    @Test
    @DisplayName("GET /games/steam - 401 sem autenticação")
    void shouldReturnUnauthorizedOnSteamWithoutAuth() throws Exception {
        mockMvc.perform(get("/games/steam").param("gameName", "Doom"))
                .andExpect(status().isUnauthorized());

        verify(gameService, never()).getSteamGames(any(String.class));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /games/epic - sucesso")
    void shouldGetEpicGamesSuccessfully() throws Exception {
        List<GenericGame> games = List.of(
                new GenericGame(
                        "123",
                        "Fortnite",
                        "https://example.com/fortnite",
                        "https://example.com/fortnite.jpg",
                        GamePlatform.EPIC,
                        0.0,
                        0.0,
                        0
                )
        );

        String gamesJson = objectMapper.writeValueAsString(games);
        when(gameService.getEpicStoreGames("Fortnite")).thenReturn(games);

        mockMvc.perform(get("/games/epic")
                        .header("Authorization", "Bearer " + testToken)
                        .param("gameName", "Fortnite"))
                .andExpect(status().isOk())
                .andExpect(content().json(gamesJson));

        verify(gameService, times(1)).getEpicStoreGames("Fortnite");
    }

    @Test
    @DisplayName("GET /games/epic - 401 sem autenticação")
    void shouldReturnUnauthorizedOnEpicWithoutAuth() throws Exception {
        mockMvc.perform(get("/games/epic").param("gameName", "Fortnite"))
                .andExpect(status().isUnauthorized());

        verify(gameService, never()).getEpicStoreGames(any(String.class));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /games/review - sucesso")
    void shouldGetGameReview() throws Exception {
        Review review = new Review(UUID.randomUUID(), "Doom", false, new Date(), new Date());
        String reviewJson = objectMapper.writeValueAsString(review);

        when(gameService.getGameReview("Doom")).thenReturn(review);

        mockMvc.perform(get("/games/review")
                        .header("Authorization", "Bearer " + testToken)
                        .param("gameName", "Doom"))
                .andExpect(status().isOk())
                .andExpect(content().json(reviewJson));

        verify(gameService, times(1)).getGameReview("Doom");
    }

    @Test
    @DisplayName("GET /games/review - 401 sem autenticação")
    void shouldReturnUnauthorizedOnReviewWithoutAuth() throws Exception {
        mockMvc.perform(get("/games/review")
                        .param("gameName", "Doom"))
                .andExpect(status().isUnauthorized());

        verify(gameService, never()).getGameReview(any(String.class));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /games/review - 404 não encontrado")
    void shouldReturnNotFoundWhenGameHasNoReview() throws Exception {
        when(gameService.getGameReview("Doom"))
                .thenThrow(new NotFoundException("No review found for game with name Doom."));

        mockMvc.perform(get("/games/review")
                        .header("Authorization", "Bearer " + testToken)
                        .param("gameName", "Doom"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No review found for game with name Doom."));

        verify(gameService, times(1)).getGameReview("Doom");
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("POST /games/review - sucesso")
    void shouldCreateGameReview() throws Exception {
        CreateReview body = new CreateReview("Doom", "Excelente jogo");
        Review response = new Review(UUID.randomUUID(), "Excelente jogo", false, new Date(), new Date());
        String responseJson = objectMapper.writeValueAsString(response);
        when(gameService.reviewGame(any())).thenReturn(response);

        mockMvc.perform(post("/games/review")
                        .with(csrf())
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));

        verify(gameService, times(1)).reviewGame(any(CreateReview.class));
    }

    @Test
    @DisplayName("POST /games/review - 401 sem autenticação")
    void shouldReturnUnauthorizedOnCreateReviewWithoutAuth() throws Exception {
        CreateReview body = new CreateReview("Doom", "Excelente jogo");

        mockMvc.perform(post("/games/review")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized());

        verify(gameService, never()).reviewGame(any(CreateReview.class));
    }

    @Test
    @WithMockUser(authorities = "USER")
    @DisplayName("POST /games/review - permissões insuficientes")
    void shouldReturn403WhenCreatingReviewWhileNotAdmin() throws Exception {
        testUser.setRoles(List.of(new Role(UUID.randomUUID(), "USER")));

        CreateReview body = new CreateReview("Doom", "Excelente jogo");

        mockMvc.perform(post("/games/review")
                        .with(csrf())
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Você não possui permissão para acessar este recurso."));

        verify(gameService, never()).reviewGame(any(CreateReview.class));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("POST /games/review - conflito")
    void shouldReturn409WhenGameReviewAlreadyExists() throws Exception {
        CreateReview body = new CreateReview("Doom", "Excelente jogo");
        when(gameService.reviewGame(any())).thenThrow(new ConflictException("Review for this game already exists."));

        mockMvc.perform(post("/games/review")
                        .with(csrf())
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Review for this game already exists."));

        verify(gameService, times(1)).reviewGame(any(CreateReview.class));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("POST /games/review - sucesso")
    void shouldUpdateGameReview() throws Exception {
        UUID reviewId = UUID.randomUUID();
        UpdateReview body = new UpdateReview("Excelente jogo");
        Review response = new Review(reviewId, "Excelente jogo", false, new Date(), new Date());
        String responseJson = objectMapper.writeValueAsString(response);
        when(gameService.updateGameReview(any(), any())).thenReturn(response);

        mockMvc.perform(put("/games/review/" + reviewId)
                        .with(csrf())
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));

        verify(gameService, times(1)).updateGameReview(any(UUID.class), any(UpdateReview.class));
    }

    @Test
    @DisplayName("PUT /games/review/{id} - 401 sem autenticação")
    void shouldReturnUnauthorizedOnUpdateReviewWithoutAuth() throws Exception {
        UUID reviewId = UUID.randomUUID();
        UpdateReview body = new UpdateReview("Nova análise");
        String bodyJson = objectMapper.writeValueAsString(body);

        mockMvc.perform(put("/games/review/" + reviewId)
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(bodyJson))
                .andExpect(status().isUnauthorized());

        verify(gameService, never()).updateGameReview(any(UUID.class), any(UpdateReview.class));
    }

    @Test
    @WithMockUser(authorities = "USER")
    @DisplayName("PUT /games/review/{id} - 403 permissões insuficientes")
    void shouldReturnForbiddenOnUpdateReviewWithoutAdmin() throws Exception {
        testUser.setRoles(List.of(new Role(UUID.randomUUID(), "USER")));

        UUID reviewId = UUID.randomUUID();
        UpdateReview body = new UpdateReview("Nova análise");
        String bodyJson = objectMapper.writeValueAsString(body);

        mockMvc.perform(put("/games/review/" + reviewId)
                        .with(csrf())
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(APPLICATION_JSON)
                        .content(bodyJson))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Você não possui permissão para acessar este recurso."));

        verify(gameService, never()).updateGameReview(any(UUID.class), any(UpdateReview.class));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("DELETE /games/review/{id} - sucesso")
    void shouldDeleteReview() throws Exception {
        UUID reviewId = UUID.randomUUID();
        doNothing().when(gameService).deleteGameReview(reviewId);

        mockMvc.perform(delete("/games/review/" + reviewId)
                        .with(csrf())
                        .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Review successfully deleted."));

        verify(gameService, times(1)).deleteGameReview(reviewId);
    }

    @Test
    @DisplayName("DELETE /games/review/{id} - 401 sem autenticação")
    void shouldReturnUnauthorizedOnDeleteReviewWithoutAuth() throws Exception {
        UUID reviewId = UUID.randomUUID();
        mockMvc.perform(delete("/games/review/" + reviewId)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());

        verify(gameService, never()).deleteGameReview(reviewId);
    }

    @Test
    @WithMockUser(authorities = "USER")
    @DisplayName("DELETE /games/review/{id} - 403 permissões insuficientes")
    void shouldReturnForbiddenWhenDeletingGameReviewWithoutAdmin() throws Exception {
        testUser.setRoles(List.of(new Role(UUID.randomUUID(), "USER")));
        UUID reviewId = UUID.randomUUID();

        mockMvc.perform(delete("/games/review/" + reviewId)
                        .with(csrf())
                        .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Você não possui permissão para acessar este recurso."));

        verify(gameService, never()).deleteGameReview(reviewId);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("DELETE /games/review/{id} - 404 não encontrado")
    void shouldReturnNotFoundWhenReviewDoesNotExist() throws Exception {
        UUID reviewId = UUID.randomUUID();
        doThrow(new NotFoundException("Review not found."))
                .when(gameService).deleteGameReview(reviewId);

        mockMvc.perform(delete("/games/review/" + reviewId)
                        .with(csrf())
                        .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Review not found."));

        verify(gameService, times(1)).deleteGameReview(reviewId);
    }
}
