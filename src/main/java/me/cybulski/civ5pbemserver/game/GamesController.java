package me.cybulski.civ5pbemserver.game;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
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
}
