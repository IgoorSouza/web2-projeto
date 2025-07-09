package com.igorsouza.games.controllers;

import com.igorsouza.games.dtos.games.CreateReview;
import com.igorsouza.games.dtos.games.GenericGame;
import com.igorsouza.games.dtos.games.Review;
import com.igorsouza.games.dtos.games.UpdateReview;
import com.igorsouza.games.exceptions.ConflictException;
import com.igorsouza.games.exceptions.NotFoundException;
import com.igorsouza.games.exceptions.UnauthorizedException;
import com.igorsouza.games.services.games.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GamesController {

    private final GameService gameService;

    @GetMapping("/steam")
    public ResponseEntity<List<GenericGame>> getGames(@RequestParam String gameName) throws UnauthorizedException {
        List<GenericGame> games = gameService.getSteamGames(gameName);
        return ResponseEntity.ok(games);
    }

    @GetMapping("/epic")
    public ResponseEntity<List<GenericGame>> getEpicGamesStoreGames(@RequestParam String gameName) throws UnauthorizedException {
        List<GenericGame> games = gameService.getEpicStoreGames(gameName);
        return ResponseEntity.ok(games);
    }

    @GetMapping("/review")
    public ResponseEntity<Review> getGameReview(@RequestParam String gameName) throws NotFoundException {
        Review review = gameService.getGameReview(gameName);
        return ResponseEntity.ok(review);
    }

    @PostMapping("/generate-review")
    public ResponseEntity<Review> generateGameReview(@RequestParam String gameName)
            throws ConflictException, InterruptedException {
        Review review = gameService.generateGameReview(gameName);
        return ResponseEntity.ok(review);
    }

    @PostMapping("/review")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Review> reviewGame(@RequestBody CreateReview createReview) throws ConflictException {
        Review review = gameService.reviewGame(createReview);
        return ResponseEntity.ok(review);
    }

    @PutMapping("/review/{reviewId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Review> updateGameReview(@PathVariable UUID reviewId, @RequestBody UpdateReview updateReview)
            throws NotFoundException {
        Review updatedReview = gameService.updateGameReview(reviewId, updateReview);
        return ResponseEntity.ok(updatedReview);
    }

    @DeleteMapping("/review/{reviewId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<String> deleteGameReview(@PathVariable UUID reviewId) throws NotFoundException {
        gameService.deleteGameReview(reviewId);
        return ResponseEntity.ok("Review successfully deleted.");
    }
}
