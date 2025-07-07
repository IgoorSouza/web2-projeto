package com.igorsouza.games.repositories;

import com.igorsouza.games.models.GameReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GameReviewRepository extends JpaRepository<GameReview, UUID> {
    Optional<GameReview> findByGameName(String gameName);
}
