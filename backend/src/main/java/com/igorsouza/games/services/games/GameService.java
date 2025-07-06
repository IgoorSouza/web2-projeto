package com.igorsouza.games.services.games;

import com.igorsouza.games.dtos.games.GenericGame;
import com.igorsouza.games.exceptions.ConflictException;
import com.igorsouza.games.exceptions.NotFoundException;
import com.igorsouza.games.exceptions.UnauthorizedException;
import com.igorsouza.games.models.Game;
import com.igorsouza.games.models.User;

import java.util.List;

public interface GameService {
    List<GenericGame> getSteamGames(String gameName) throws UnauthorizedException;
    GenericGame getSteamGameById(String identifier);
    List<GenericGame> getEpicStoreGames(String gameName) throws UnauthorizedException;
    GenericGame getEpicStoreGameById(String identifier);
    List<Game> getGamesByUser(User user);
    void saveGame(Game game) throws ConflictException;
    void removeGame(Game game) throws NotFoundException;
}
