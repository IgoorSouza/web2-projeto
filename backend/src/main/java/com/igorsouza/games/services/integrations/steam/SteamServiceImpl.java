package com.igorsouza.games.services.integrations.steam;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.igorsouza.games.dtos.games.steam.SteamGame;
import com.igorsouza.games.dtos.games.steam.SteamGameDetails;
import com.igorsouza.games.dtos.games.steam.SteamGameSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SteamServiceImpl implements SteamService {

    private final ObjectMapper objectMapper;

    @Override
    public List<SteamGameDetails> getGames(String gameName) {
        List<Integer> gamesIds = getGamesIdsByName(gameName);
        return gamesIds.stream().map(this::getGameDetails).toList();
    }

    public SteamGameDetails getGameDetails(Integer gameId) {
        String url = "https://store.steampowered.com/api/appdetails?cc=br&l=portuguese&appids=" + gameId;
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> gameDetailsResponse = restTemplate.getForObject(url, Map.class);
        Map<String, Object> gameDetailsWrapper = (Map<String, Object>) gameDetailsResponse.get(String.valueOf(gameId));
        SteamGameDetails gameDetails = objectMapper.convertValue(gameDetailsWrapper.get("data"), SteamGameDetails.class);

        gameDetails.setIdentifier(String.valueOf(gameId));
        gameDetails.setUrl("https://store.steampowered.com/app/" + gameId);
        return gameDetails;
    }

    private List<Integer> getGamesIdsByName(String gameName) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://store.steampowered.com/api/storesearch?cc=br&l=portuguese&term=" + gameName;
        SteamGameSearchResponse response = restTemplate.getForObject(url, SteamGameSearchResponse.class);

        return response.getItems().stream().map(SteamGame::getId).toList();
    }
}
