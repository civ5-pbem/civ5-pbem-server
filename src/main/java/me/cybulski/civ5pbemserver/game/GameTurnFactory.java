package me.cybulski.civ5pbemserver.game;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.cybulski.civ5pbemserver.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author Michał Cybulski
 */
@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class GameTurnFactory {

    private final GameTurnRepository gameTurnRepository;

    public GameTurn createFirstGameTurn(Game game) {
        Assert.state(GameState.WAITING_FOR_FIRST_MOVE.equals(game.getGameState()),
                     "Game must be in WAITING_FOR_FIRST_MOVE to add first turn");
        GameTurn firstGameTurn = GameTurn.builder()
                .currentPlayer(game.findPlayer(game.getHost()).orElseThrow(ResourceNotFoundException::new))
                .game(game)
                .previousGameTurn(null)
                .saveFilename(null)
                .turnNumber(0)
                .build();
        firstGameTurn = gameTurnRepository.save(firstGameTurn);

        return firstGameTurn;
    }

    public GameTurn createNextTurn(GameTurn previousGameTurn, List<Player> players, String saveFilename) {
        Player nextHumanPlayer = players.stream()
                .filter(humanPlayerPredicate())
                .filter(player -> player.getPlayerNumber() > previousGameTurn.getCurrentPlayer().getPlayerNumber())
                .findFirst()
                .orElseGet(() -> players.stream()
                        .filter(humanPlayerPredicate())
                        .min(Comparator.comparingInt(Player::getPlayerNumber))
                        .orElseThrow(RuntimeException::new));

        GameTurn nextGameTurn = GameTurn.builder()
                .game(previousGameTurn.getGame())
                .currentPlayer(nextHumanPlayer)
                .turnNumber(previousGameTurn.getCurrentPlayer().getPlayerNumber() >= nextHumanPlayer.getPlayerNumber()
                                    ? previousGameTurn.getTurnNumber() + 1
                                    : previousGameTurn.getTurnNumber())
                .saveFilename(saveFilename)
                .previousGameTurn(previousGameTurn)
                .build();
        nextGameTurn = gameTurnRepository.save(nextGameTurn);

        return nextGameTurn;
    }

    private Predicate<Player> humanPlayerPredicate() {
        return player -> PlayerType.HUMAN.equals(player.getPlayerType());
    }
}
