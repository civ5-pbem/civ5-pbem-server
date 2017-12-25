package me.cybulski.civ5pbemserver.game;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.cybulski.civ5pbemserver.game.dto.ChangePlayerTypeInputDTO;
import me.cybulski.civ5pbemserver.game.dto.ChooseCivilizationInputDTO;
import me.cybulski.civ5pbemserver.game.dto.GameOutputDTO;
import me.cybulski.civ5pbemserver.game.dto.NewGameInputDTO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Michał Cybulski
 */
@RestController
@RequestMapping("games")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class GamesController {

    private final GameApplicationService gameApplicationService;

    @RequestMapping(path = "new-game", method = RequestMethod.POST, consumes = "application/json")
    public GameOutputDTO createNewGame(@Validated @RequestBody NewGameInputDTO newGameInputDTO) {
        return gameApplicationService.createNewGame(newGameInputDTO);
    }

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public List<GameOutputDTO> findAllGames() {
        return gameApplicationService.findAllGames();
    }

    @RequestMapping(path = "{gameId}", method = RequestMethod.GET)
    public GameOutputDTO findGameById(@PathVariable String gameId) {
        return gameApplicationService.findGameById(gameId);
    }

    @RequestMapping(path = "{gameId}/players/{playerId}/change-player-type", method = RequestMethod.POST)
    public GameOutputDTO changePlayerType(@PathVariable String gameId,
                                          @PathVariable String playerId,
                                          @Validated @RequestBody ChangePlayerTypeInputDTO changePlayerTypeInputDTO) {
        return gameApplicationService.changePlayerType(gameId, playerId, changePlayerTypeInputDTO);
    }

    @RequestMapping(path = "{gameId}/players/{playerId}/choose-civilization", method = RequestMethod.POST)
    public GameOutputDTO chooseCivilization(@PathVariable String gameId,
                                            @PathVariable String playerId,
                                            @Validated @RequestBody ChooseCivilizationInputDTO chooseCivilizationInputDTO) {
        return gameApplicationService.chooseCivilization(gameId, playerId, chooseCivilizationInputDTO);
    }

    @RequestMapping(path = "{gameId}/players/{playerId}/kick", method = RequestMethod.POST)
    public GameOutputDTO kickPlayer(@PathVariable String gameId,
                                            @PathVariable String playerId) {
        return gameApplicationService.kickPlayer(gameId, playerId);
    }

    @RequestMapping(path = "{gameId}/leave", method = RequestMethod.POST)
    public GameOutputDTO leaveGame(@PathVariable String gameId) {
        return gameApplicationService.leaveGame(gameId);
    }

    @RequestMapping(path = "{gameId}/join", method = RequestMethod.POST)
    public GameOutputDTO joinGame(@PathVariable String gameId) {
        return gameApplicationService.joinGame(gameId);
    }
}
