package me.cybulski.civ5pbemserver.game;

import me.cybulski.civ5pbemserver.user.UserAccount;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Michał Cybulski
 */
@Component
class PlayerFactory {

    public Set<Player> createNewPlayers(UserAccount host, Game game, int playerCount) {
        Set<Player> result = new HashSet<>();

        for (int i = 0; i < playerCount; i++) {
            result.add(Player.builder()
                               .civilization(Civilization.RANDOM)
                               .playerNumber(i + 1)
                               .humanUserAccount(i == 0 ? host : null)
                               .playerType(PlayerType.HUMAN)
                               .game(game)
                               .build());
        }

        return result;
    }
}
