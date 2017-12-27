package me.cybulski.civ5pbemserver.game;

import me.cybulski.civ5pbemserver.game.exception.NoPermissionToModifyGameException;
import me.cybulski.civ5pbemserver.user.UserAccount;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class CurrentGameTurnValidator {

    void checkCurrentTurnOrThrow(Game game, UserAccount currentUser) {
        Optional<GameTurn> currentGameTurnOptional = game.getCurrentGameTurn();
        if (!currentGameTurnOptional.isPresent()
                || !currentUser.equals(currentGameTurnOptional.get().getCurrentPlayer().getHumanUserAccount())) {
            throw new NoPermissionToModifyGameException("This is not your turn!");
        }
    }
}
