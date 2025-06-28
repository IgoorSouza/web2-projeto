package com.igorsouza.games.repositories;

import com.igorsouza.games.models.Game;
import com.igorsouza.games.models.GameId;
import com.igorsouza.games.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GamesRepository extends JpaRepository<Game, GameId> {
    List<Game> findAllByUser(User user);
}
