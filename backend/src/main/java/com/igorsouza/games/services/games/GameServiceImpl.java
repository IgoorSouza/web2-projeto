package com.igorsouza.games.services.games;

import com.igorsouza.games.dtos.games.GenericGame;
import com.igorsouza.games.dtos.games.epic.*;
import com.igorsouza.games.dtos.games.steam.SteamGameDetails;
import com.igorsouza.games.dtos.games.steam.SteamGamePriceOverview;
import com.igorsouza.games.enums.GamePlatform;
import com.igorsouza.games.exceptions.ConflictException;
import com.igorsouza.games.exceptions.NotFoundException;
import com.igorsouza.games.exceptions.UnauthorizedException;
import com.igorsouza.games.models.Game;
import com.igorsouza.games.models.GameId;
import com.igorsouza.games.models.User;
import com.igorsouza.games.repositories.GamesRepository;
import com.igorsouza.games.services.integrations.epic.EpicGamesStoreService;
import com.igorsouza.games.services.integrations.steam.SteamService;
import com.igorsouza.games.services.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final SteamService steamService;
    private final EpicGamesStoreService epicGamesStoreService;
    private final UserService userService;
    private final GamesRepository gamesRepository;

    @Override
    public List<GenericGame> getSteamGames(String gameName) throws UnauthorizedException {
        saveGameSearch(gameName, GamePlatform.STEAM);
        List<SteamGameDetails> games = steamService.getGames(gameName);
        return games.stream().map(this::formatSteamGame).toList();
    }

    @Override
    public List<GenericGame> getEpicStoreGames(String gameName) throws UnauthorizedException {
        saveGameSearch(gameName, GamePlatform.EPIC);
        List<EpicGamesStoreGame> games = epicGamesStoreService.getGames(gameName);
        List<EpicGamesStoreGame> availableGames = filterAvailableEpicStoreGames(games);
        return availableGames.stream().map(this::formatEpicStoreGame).toList();
    }

    @Override
    public GenericGame getSteamGameById(String identifier) {
        SteamGameDetails steamGame = steamService.getGameDetails(Integer.parseInt(identifier));
        return formatSteamGame(steamGame);
    }

    @Override
    public GenericGame getEpicStoreGameById(String identifier) {
        EpicGamesStoreGame epicGame = epicGamesStoreService.getGameDetails(identifier);
        return formatEpicStoreGame(epicGame);
    }

    @Override
    public List<Game> getGamesByUser(User user) {
        return gamesRepository.findAllByUser(user);
    }

    @Override
    public void saveGame(Game game) throws ConflictException {
        GameId gameId = new GameId(game.getUserId(), game.getPlatformIdentifier(), game.getPlatform());

        if (gamesRepository.existsById(gameId)) {
            throw new ConflictException("You have already added this game to your wishlist.");
        }

        gamesRepository.save(game);
    }

    @Override
    public void removeGame(Game game) throws NotFoundException {
        GameId gameId = new GameId(game.getUserId(), game.getPlatformIdentifier(), game.getPlatform());
        boolean gameExists = gamesRepository.existsById(gameId);

        if (!gameExists) {
            throw new NotFoundException("Game not found.");
        }

        gamesRepository.deleteById(gameId);
    }

    private void saveGameSearch(String gameName, GamePlatform platform) throws UnauthorizedException {
        userService.saveUserGameSearch(gameName, platform);
    }

    private GenericGame formatSteamGame(SteamGameDetails game) {
        SteamGamePriceOverview gamePrice = game.getPriceOverview();
        double initialPrice = gamePrice == null ? 0 : (double) gamePrice.getInitialPrice() / 100;
        double discountPrice = gamePrice == null ? 0 : (double) gamePrice.getFinalPrice() / 100;
        int discountPercent = gamePrice == null ? 0 : gamePrice.getDiscountPercent();

        return new GenericGame(
                game.getIdentifier(),
                game.getName(),
                game.getUrl(),
                game.getHeaderImage(),
                GamePlatform.STEAM,
                initialPrice,
                discountPrice,
                discountPercent
        );
    }

    private List<EpicGamesStoreGame> filterAvailableEpicStoreGames(List<EpicGamesStoreGame> games) {
        return games.stream()
                .filter(game -> {
                    for (EpicGamesStoreGameCategory category : game.getCategories()) {
                        String path = category.getPath();
                        if (path.equals("testing")) return false;
                        if (path.equals("addons")) return true;
                    }

                    List<EpicGamesStoreGameCatalogNsMapping> catalogNsMappings = game.getCatalogNs().getMappings();
                    if (catalogNsMappings == null) return false;

                    return catalogNsMappings.stream()
                            .anyMatch(mapping -> "productHome".equals(mapping.getPageType()));
                }).toList();
    }

    private GenericGame formatEpicStoreGame(EpicGamesStoreGame game) {
        String gameSlug = getGameSlug(game);
        List<EpicGamesStoreGameImage> gameImages = game.getKeyImages();
        EpicGamesStoreGameTotalPrice gamePrice = game.getPrice().getTotalPrice();
        double initialPrice = (double) gamePrice.getOriginalPrice() / 100;
        double discountPrice = (double) gamePrice.getDiscountPrice() / 100;
        int discountPercent = initialPrice == discountPrice
                ? 0
                : (int) (100 - ((discountPrice * 100) / initialPrice));

        return new GenericGame(
                game.getTitle(),
                game.getTitle(),
                gameSlug == null ? null : "https://store.epicgames.com/pt-BR/p/" + gameSlug,
                gameImages.isEmpty() ? null : gameImages.getFirst().getUrl(),
                GamePlatform.EPIC,
                initialPrice,
                discountPrice,
                discountPercent
        );
    }

    private String getGameSlug(EpicGamesStoreGame game) {
        for (EpicGamesStoreGameCategory category : game.getCategories()) {
            if (category.getPath().equals("addons")) {
                return game.getUrlSlug();
            }
        }

        String productSlug = game.getProductSlug();
        if (productSlug != null) return productSlug.replace("/", "--");

        return game.getCatalogNs().getMappings().stream()
                .filter(mapping -> "productHome".equals(mapping.getPageType()))
                .findFirst()
                .orElseThrow()
                .getPageSlug();
    }
}
