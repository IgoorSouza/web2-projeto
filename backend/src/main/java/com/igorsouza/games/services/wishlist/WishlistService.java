package com.igorsouza.games.services.wishlist;

import com.igorsouza.games.dtos.games.GenericGame;
import com.igorsouza.games.dtos.games.WishlistGame;
import com.igorsouza.games.exceptions.ConflictException;
import com.igorsouza.games.exceptions.NotFoundException;
import com.igorsouza.games.exceptions.UnauthorizedException;

import java.util.List;

public interface WishlistService {
    List<GenericGame> getAuthenticatedUserGames() throws UnauthorizedException;
    void addGame(WishlistGame wishlistGame) throws ConflictException, UnauthorizedException;
    void removeGame(WishlistGame wishlistGame) throws NotFoundException, UnauthorizedException;
}
