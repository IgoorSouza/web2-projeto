package com.igorsouza.games.services.integrations.epic;

import com.igorsouza.games.dtos.games.epic.EpicGamesStoreGame;

import java.util.List;

public interface EpicGamesStoreService {
    List<EpicGamesStoreGame> getGames(String gameName);
    EpicGamesStoreGame getGameDetails(String identifier);
}
