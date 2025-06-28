package com.igorsouza.games.schedulers;

import com.igorsouza.games.dtos.games.GenericGame;
import com.igorsouza.games.enums.GamePlatform;
import com.igorsouza.games.models.Game;
import com.igorsouza.games.models.User;
import com.igorsouza.games.services.games.GameService;
import com.igorsouza.games.services.mail.MailService;
import com.igorsouza.games.services.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GameDiscountWarningScheduler {

    private final UserService userService;
    private final GameService gameService;
    private final MailService mailService;

    @Scheduled(fixedRate = 86400000, initialDelay = 0, zone = "America/Sao_Paulo")
    public void sendGameDiscountWarningMail() {
        List<User> users = userService.getUsersWithVerifiedEmailAndEnabledNotifications();

        for (User user : users) {
            List<Game> userGames = gameService.getGamesByUser(user);
            List<GenericGame> gamesWithDiscount = new ArrayList<>();

            for (Game game : userGames) {
                GamePlatform gamePlatform = game.getPlatform();
                GenericGame genericGame = gamePlatform.equals(GamePlatform.STEAM)
                        ? gameService.getSteamGameById(game.getPlatformIdentifier())
                        : gameService.getEpicStoreGameById(game.getPlatformIdentifier());

                if (genericGame.getDiscountPercent() > 0) {
                    gamesWithDiscount.add(genericGame);
                }
            }

            if (!gamesWithDiscount.isEmpty()) {
                mailService.sendDiscountWarningMail(
                        user.getEmail(),
                        "📉 Seus jogos estão em promoção!",
                        gamesWithDiscount
                );
            }
        }
    }
}
