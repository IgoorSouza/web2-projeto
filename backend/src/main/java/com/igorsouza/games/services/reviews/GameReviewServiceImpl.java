package com.igorsouza.games.services.reviews;

import com.igorsouza.games.dtos.games.Review;
import com.igorsouza.games.exceptions.ConflictException;
import com.igorsouza.games.exceptions.NotFoundException;
import com.igorsouza.games.models.GameReview;
import com.igorsouza.games.repositories.GameReviewRepository;
import com.igorsouza.games.services.azure.AzureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameReviewServiceImpl implements GameReviewService {

    private final AzureService azureService;
    private final GameReviewRepository gameReviewRepository;

    public Review generateGameReview(String gameName) throws ConflictException, InterruptedException {
        String normalizedGameName = normalizeGameName(gameName);
        Optional<GameReview> existingReview = gameReviewRepository.findByGameName(normalizedGameName);

        if (existingReview.isPresent()) {
            throw new ConflictException("Review for this game already exists.");
        }

        String review = azureService.sendMessageToAssistant("Faça uma review detalhada do jogo " + gameName);
        GameReview gameReview = GameReview.builder()
                .gameName(normalizedGameName)
                .review(review)
                .aiGenerated(true)
                .build();

        gameReview = gameReviewRepository.save(gameReview);

        return new Review(
                gameReview.getId(),
                gameReview.getReview(),
                gameReview.isAiGenerated(),
                gameReview.getCreatedAt(),
                gameReview.getUpdatedAt()
        );
    }

    public Review getGameReview(String gameName) throws NotFoundException {
        Optional<GameReview> gameReviewOptional = gameReviewRepository.findByGameName(normalizeGameName(gameName));

        if (gameReviewOptional.isEmpty()) {
            throw new NotFoundException("No review found for game with name " + gameName + ".");
        }

        GameReview gameReview = gameReviewOptional.get();
        return new Review(
                gameReview.getId(),
                gameReview.getReview(),
                gameReview.isAiGenerated(),
                gameReview.getCreatedAt(),
                gameReview.getUpdatedAt()
        );
    }

    public Review reviewGame(String gameName, String review) throws ConflictException {
        Optional<GameReview> existingReview = gameReviewRepository.findByGameName(normalizeGameName(gameName));

        if (existingReview.isPresent()) {
            throw new ConflictException("Review for this game already exists.");
        }

        GameReview gameReview = GameReview.builder()
                .gameName(normalizeGameName(gameName))
                .review(review)
                .aiGenerated(false)
                .build();

        gameReview = gameReviewRepository.save(gameReview);

        return new Review(
                gameReview.getId(),
                gameReview.getReview(),
                gameReview.isAiGenerated(),
                gameReview.getCreatedAt(),
                gameReview.getUpdatedAt()
        );
    }

    public Review updateGameReview(UUID id, String review) throws NotFoundException {
        GameReview gameReview = gameReviewRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Review not found."));

        gameReview.setReview(review);
        gameReview.setAiGenerated(false);
        gameReview = gameReviewRepository.save(gameReview);

        return new Review(
                gameReview.getId(),
                gameReview.getReview(),
                gameReview.isAiGenerated(),
                gameReview.getCreatedAt(),
                gameReview.getUpdatedAt()
        );
    }

    public void deleteGameReview(UUID id) throws NotFoundException {
        GameReview gameReview = gameReviewRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Review not found."));

        gameReviewRepository.delete(gameReview);
    }

    private String normalizeGameName(String gameName) {
        return gameName.trim().toLowerCase();
    }
}
