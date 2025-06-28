package com.igorsouza.games.services.wishlist;

import com.igorsouza.games.dtos.games.GenericGame;
import com.igorsouza.games.dtos.games.WishlistGame;
import com.igorsouza.games.enums.GamePlatform;
import com.igorsouza.games.exceptions.ConflictException;
import com.igorsouza.games.exceptions.NotFoundException;
import com.igorsouza.games.exceptions.UnauthorizedException;
import com.igorsouza.games.models.Game;
import com.igorsouza.games.models.GameId;
import com.igorsouza.games.models.User;
import com.igorsouza.games.services.games.GameService;
import com.igorsouza.games.services.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final UserService userService;
    private final GameService gameService;

    @Override
    public List<GenericGame> getAuthenticatedUserGames() throws UnauthorizedException {
        User authenticatedUser = userService.getAuthenticatedUser();
        List<Game> userGames = gameService.getGamesByUser(authenticatedUser);

        return userGames.stream().map(game -> {
            if (game.getPlatform().equals(GamePlatform.STEAM)) {
                return gameService.getSteamGameById(game.getPlatformIdentifier());
            }

            return gameService.getEpicStoreGameById(game.getPlatformIdentifier());
        }).toList();
    }

    @Override
    public void addGame(WishlistGame wishlistGame) throws ConflictException, UnauthorizedException {
        Game game = parseWishlistGameToGame(wishlistGame);
        gameService.saveGame(game);
    }

    @Override
    public void removeGame(WishlistGame wishlistGame) throws NotFoundException, UnauthorizedException {
        Game game = parseWishlistGameToGame(wishlistGame);
        gameService.removeGame(game);
    }

    private Game parseWishlistGameToGame(WishlistGame wishlistGame) throws UnauthorizedException {
        User authenticatedUser = userService.getAuthenticatedUser();
        GameId gameId = new GameId(
                authenticatedUser.getId(),
                wishlistGame.getPlatformIdentifier(),
                wishlistGame.getPlatform()
        );

        return new Game(gameId, authenticatedUser);
    }
}
