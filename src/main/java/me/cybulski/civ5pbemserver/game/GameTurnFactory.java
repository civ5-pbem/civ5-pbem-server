package me.cybulski.civ5pbemserver.game;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.cybulski.civ5pbemserver.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Comparator;
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
        game.nextTurn(firstGameTurn);

        return firstGameTurn;
    }

    public GameTurn createNextTurn(GameTurn previousGameTurn, String saveFilename) {
        Game game = previousGameTurn.getGame();
        boolean isFirstMoveBeingFinished = GameState.WAITING_FOR_FIRST_MOVE.equals(game.getGameState())
                && previousGameTurn.getPreviousGameTurn() == null;
        Assert.state(GameState.IN_PROGRESS.equals(game.getGameState()) || isFirstMoveBeingFinished,
                     "Game must be in IN_PROGRESS or it must be first turn to add next turn");

        Player nextHumanPlayer = game.getPlayerList().stream()
                .filter(humanPlayerPredicate())
                .filter(player -> player.getPlayerNumber() > previousGameTurn.getCurrentPlayer().getPlayerNumber())
                .findFirst()
                .orElseGet(() -> game.getPlayerList().stream()
                        .filter(humanPlayerPredicate())
                        .min(Comparator.comparingInt(Player::getPlayerNumber))
                        .orElseThrow(RuntimeException::new));

        GameTurn nextGameTurn = GameTurn.builder()
                .game(game)
                .currentPlayer(nextHumanPlayer)
                .turnNumber(previousGameTurn.getCurrentPlayer().getPlayerNumber() >= nextHumanPlayer.getPlayerNumber()
                                    ? previousGameTurn.getTurnNumber() + 1
                                    : previousGameTurn.getTurnNumber())
                .saveFilename(saveFilename)
                .previousGameTurn(previousGameTurn)
                .build();
        nextGameTurn = gameTurnRepository.save(nextGameTurn);
        game.nextTurn(nextGameTurn);

        return nextGameTurn;
    }

    private Predicate<Player> humanPlayerPredicate() {
        return player -> PlayerType.HUMAN.equals(player.getPlayerType());
    }
}
