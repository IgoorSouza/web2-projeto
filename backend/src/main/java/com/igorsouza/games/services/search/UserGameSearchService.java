package com.igorsouza.games.services.search;

import com.igorsouza.games.enums.GamePlatform;
import com.igorsouza.games.models.User;
import com.igorsouza.games.models.UserGameSearch;

import java.util.List;
import java.util.UUID;

public interface UserGameSearchService {
    void saveSearch(String gameName, GamePlatform platform, User user);
    List<UserGameSearch> getUserSearches(UUID userId);
}
