package com.igorsouza.games.services.integrations.steam;

import com.igorsouza.games.dtos.games.steam.SteamGameDetails;

import java.util.List;

public interface SteamService {
    List<SteamGameDetails> getGames(String gameName);
    SteamGameDetails getGameDetails(Integer gameId);
}
