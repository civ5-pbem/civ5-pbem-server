package me.cybulski.civ5pbemserver.game;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.cybulski.civ5pbemserver.game.dto.GameOutputDTO;
import me.cybulski.civ5pbemserver.game.dto.NewGameInputDTO;
import me.cybulski.civ5pbemserver.game.dto.PlayerOutputDTO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * @author Michał Cybulski
 */
@RestController
@RequestMapping("games")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class GamesController {

    private final GameApplicationService gameApplicationService;

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(path = "new-game", method = RequestMethod.POST, consumes = "application/json")
    public GameOutputDTO createNewGame(@Validated @RequestBody NewGameInputDTO newGameInputDTO) {
        return convertToDTO(gameApplicationService.createNewGame(newGameInputDTO));
    }

    private GameOutputDTO convertToDTO(Game game) {
        return GameOutputDTO
                       .builder()
                       .name(game.getName())
                       .description(game.getDescription())
                       .gameState(game.getGameState())
                       .host(game.getHost().getUsername())
                       .mapSize(game.getMapSize())
                       .numberOfCityStates(game.getNumberOfCityStates())
                       .players(game.getPlayers()
                                        .stream()
                                        .map(this::convertToDTO)
                                        .sorted(Comparator.comparingInt(PlayerOutputDTO::getPlayerNumber))
                                        .collect(Collectors.toList()))
                       .build();
    }

    private PlayerOutputDTO convertToDTO(Player player) {
        return PlayerOutputDTO.builder()
                       .playerNumber(player.getPlayerNumber())
                       .civilization(player.getCivilization())
                       .humanUserAccount(player.getHumanUserAccount().getUsername())
                       .playerType(player.getPlayerType())
                       .build();
    }
}
