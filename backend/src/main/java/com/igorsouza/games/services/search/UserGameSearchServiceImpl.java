package com.igorsouza.games.services.search;

import com.igorsouza.games.enums.GamePlatform;
import com.igorsouza.games.models.User;
import com.igorsouza.games.models.UserGameSearch;
import com.igorsouza.games.repositories.UserGameSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserGameSearchServiceImpl implements UserGameSearchService {

    private final UserGameSearchRepository userGameSearchRepository;

    @Override
    public List<UserGameSearch> getUserSearches(UUID userId) {
        return userGameSearchRepository.findByUserId(userId);
    }

    @Override
    public void saveSearch(String gameName, GamePlatform platform, User user) {
        UserGameSearch search = UserGameSearch.builder()
                .gameName(gameName)
                .platform(platform)
                .user(user)
                .build();

        userGameSearchRepository.save(search);
    }
}
