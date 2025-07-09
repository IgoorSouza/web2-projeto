package com.igorsouza.games.wishlist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.igorsouza.games.controllers.WishlistController;
import com.igorsouza.games.dtos.games.GenericGame;
import com.igorsouza.games.dtos.games.WishlistGame;
import com.igorsouza.games.enums.GamePlatform;
import com.igorsouza.games.exceptions.ConflictException;
import com.igorsouza.games.exceptions.NotFoundException;
import com.igorsouza.games.models.User;
import com.igorsouza.games.services.jwt.JwtService;
import com.igorsouza.games.services.users.UserService;
import com.igorsouza.games.services.wishlist.WishlistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WishlistController.class)
public class WishlistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WishlistService wishlistService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserService userService;

    private String testToken;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() throws NotFoundException  {
        UUID testUserId = UUID.randomUUID();
        User testUser = new User(
                testUserId,
                "Igor",
                "igor.castro@estudante.iftm.edu.br",
                "password123",
                false,
                false,
                List.of(),
                List.of(),
                List.of()
        );

        when(userService.getUserById(any())).thenReturn(testUser);
        when(jwtService.validateToken(any())).thenReturn(String.valueOf(testUserId));
        when(jwtService.generateToken(any())).thenReturn("mocked-token");
        testToken = jwtService.generateToken(testUser.getId());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /wishlist - Deve retornar lista de jogos com sucesso")
    void shouldReturnListOfWishlistedGames() throws Exception {
        GenericGame game = new GenericGame(
                "123",
                "Game Title",
                "URL",
                "Image",
                GamePlatform.STEAM,
                0.0,
                0.0,
                0
        );
        when(wishlistService.getAuthenticatedUserGames()).thenReturn(List.of(game));

        mockMvc.perform(get("/wishlist")
                        .with(csrf())
                        .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isOk());

        verify(wishlistService, times(1)).getAuthenticatedUserGames();
    }

    @Test
    @DisplayName("GET /wishlist - Deve retornar 401 se não autenticado")
    void shouldReturnUnauthorizedWhenGettingWishlistWithoutAuth() throws Exception {
        mockMvc.perform(get("/wishlist")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /wishlist - Deve adicionar jogo com sucesso")
    void shouldAddGameToWishlist() throws Exception {
        WishlistGame game = new WishlistGame("456", GamePlatform.EPIC);
        String gameJson = objectMapper.writeValueAsString(game);

        mockMvc.perform(post("/wishlist")
                        .with(csrf())
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(APPLICATION_JSON)
                        .content(gameJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Game successfully added to the wishlist."));

        verify(wishlistService, times(1)).addGame(any(WishlistGame.class));
    }

    @Test
    @DisplayName("POST /wishlist - Deve retornar 401 se não autenticado")
    void shouldReturnUnauthorizedWhenAddingGameWithoutAuth() throws Exception {
        WishlistGame game = new WishlistGame("456", GamePlatform.EPIC);
        String gameJson = objectMapper.writeValueAsString(game);

        mockMvc.perform(post("/wishlist")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(gameJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /wishlist - Deve retornar conflito ao adicionar jogo duplicado")
    void shouldReturnConflictWhenAddingExistingGame() throws Exception {
        WishlistGame game = new WishlistGame("456", GamePlatform.EPIC);
        String gameJson = objectMapper.writeValueAsString(game);

        doThrow(new ConflictException("Game already in wishlist"))
                .when(wishlistService).addGame(any());

        mockMvc.perform(post("/wishlist")
                        .with(csrf())
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(APPLICATION_JSON)
                        .content(gameJson))
                .andExpect(status().isConflict())
                .andExpect(content().string("Game already in wishlist"));
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /wishlist - Deve remover jogo com sucesso")
    void shouldRemoveGameFromWishlist() throws Exception {
        WishlistGame game = new WishlistGame("456", GamePlatform.EPIC);
        String gameJson = objectMapper.writeValueAsString(game);

        mockMvc.perform(delete("/wishlist")
                        .with(csrf())
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(APPLICATION_JSON)
                        .content(gameJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Game successfully removed from the wishlist."));

        verify(wishlistService, times(1)).removeGame(any(WishlistGame.class));
    }

    @Test
    @DisplayName("DELETE /wishlist - Deve retornar 401 se não autenticado")
    void shouldReturnUnauthorizedWhenDeletingGameWithoutAuth() throws Exception {
        WishlistGame game = new WishlistGame("456", GamePlatform.EPIC);
        String gameJson = objectMapper.writeValueAsString(game);

        mockMvc.perform(delete("/wishlist")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(gameJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /wishlist - Deve retornar 404 se jogo não encontrado")
    void shouldReturnNotFoundWhenRemovingNonExistingGame() throws Exception {
        WishlistGame game = new WishlistGame("456", GamePlatform.EPIC);
        String gameJson = objectMapper.writeValueAsString(game);

        doThrow(new NotFoundException("Game not found"))
                .when(wishlistService).removeGame(any());

        mockMvc.perform(delete("/wishlist")
                        .with(csrf())
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(APPLICATION_JSON)
                        .content(gameJson))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Game not found"));
    }
}
