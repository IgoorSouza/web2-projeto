package com.igorsouza.games.services.reviews;

import com.igorsouza.games.dtos.games.Review;
import com.igorsouza.games.exceptions.ConflictException;
import com.igorsouza.games.exceptions.NotFoundException;

import java.util.UUID;

public interface GameReviewService {
    String generateGameReview(String gameName) throws ConflictException, InterruptedException;
    Review getGameReview(String gameName) throws NotFoundException;
    void reviewGame(String gameName, String review) throws ConflictException;
    Review updateGameReview(UUID id, String review) throws NotFoundException;
    void deleteGameReview(UUID id) throws NotFoundException;
}
