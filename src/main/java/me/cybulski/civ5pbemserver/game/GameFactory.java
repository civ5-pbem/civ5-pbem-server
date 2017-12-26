package me.cybulski.civ5pbemserver.game;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.cybulski.civ5pbemserver.game.dto.NewGameInputDTO;
import me.cybulski.civ5pbemserver.user.UserAccount;
import org.springframework.stereotype.Component;

import java.util.HashSet;

/**
 * @author Michał Cybulski
 */
@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class GameFactory {

    private final GameRepository gameRepository;
    private final PlayerFactory playerFactory;

    public Game createNewGame(UserAccount host, NewGameInputDTO newGameInputDTO) {
        MapSize mapSize = newGameInputDTO.getMapSize();
        int maxNumberOfPlayers = mapSize.getMaxNumberOfPlayers();

        Game newGame = Game.builder()
                             .name(newGameInputDTO.getGameName())
                             .description(newGameInputDTO.getGameDescription())
                             .gameState(GameState.WAITING_FOR_PLAYERS)
                             .host(host)
                             .mapSize(mapSize)
                             .numberOfCityStates(mapSize.getDefaultNumberOfCityStates())
                             .players(new HashSet<>())
                             .build();
        newGame.getPlayers().addAll(playerFactory.createNewPlayers(host, newGame, maxNumberOfPlayers));

        return gameRepository.save(newGame);
    }
}
