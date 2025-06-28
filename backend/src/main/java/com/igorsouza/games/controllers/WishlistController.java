package com.igorsouza.games.controllers;

import com.igorsouza.games.dtos.games.GenericGame;
import com.igorsouza.games.dtos.games.WishlistGame;
import com.igorsouza.games.exceptions.ConflictException;
import com.igorsouza.games.exceptions.NotFoundException;
import com.igorsouza.games.exceptions.UnauthorizedException;
import com.igorsouza.games.services.wishlist.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wishlist")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<List<GenericGame>> getWishlistedGames() throws UnauthorizedException {
        List<GenericGame> games = wishlistService.getAuthenticatedUserGames();
        return ResponseEntity.ok(games);
    }

    @PostMapping
    public ResponseEntity<String> addGameToWishlist(@RequestBody WishlistGame game) throws ConflictException, UnauthorizedException {
        wishlistService.addGame(game);
        return ResponseEntity.ok().body("Game successfully added to the wishlist.");
    }

    @DeleteMapping
    public ResponseEntity<String> removeGameFromWishlist(@RequestBody WishlistGame game) throws NotFoundException, UnauthorizedException {
        wishlistService.removeGame(game);
        return ResponseEntity.ok().body("Game successfully removed from the wishlist.");
    }
}
