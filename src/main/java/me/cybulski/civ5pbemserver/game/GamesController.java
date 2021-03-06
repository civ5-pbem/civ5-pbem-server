package me.cybulski.civ5pbemserver.game;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.cybulski.civ5pbemserver.game.dto.ChangePlayerTypeInputDTO;
import me.cybulski.civ5pbemserver.game.dto.ChooseCivilizationInputDTO;
import me.cybulski.civ5pbemserver.game.dto.GameOutputDTO;
import me.cybulski.civ5pbemserver.game.dto.NewGameInputDTO;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    @RequestMapping(method = RequestMethod.GET)
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

    @RequestMapping(path = "{gameId}/start", method = RequestMethod.POST)
    public GameOutputDTO startGame(@PathVariable String gameId) {
        return gameApplicationService.startGame(gameId);
    }

    @RequestMapping(path = "{gameId}/finish-turn", method = RequestMethod.POST)
    public GameOutputDTO finishTurn(@PathVariable String gameId,
                                    @RequestParam("file") MultipartFile multipartFile)
            throws IOException {
        return gameApplicationService.finishTurn(gameId, multipartFile);
    }

    @RequestMapping(path = "{gameId}/save-game", method = RequestMethod.GET)
    public void getSaveGame(@PathVariable String gameId, HttpServletResponse httpServletResponse) throws IOException {
        Resource generatedSaveGame =  gameApplicationService.generateDynamicSaveGameForTurn(gameId);
        File saveGameFile = generatedSaveGame.getFile();
        httpServletResponse.setHeader("Content-Length", Long.toString(saveGameFile.length()));

        InputStream inputStream = generatedSaveGame.getInputStream();
        OutputStream targetOutputStream = httpServletResponse.getOutputStream();
        IOUtils.copy(inputStream, targetOutputStream);
        inputStream.close();
        targetOutputStream.close();

        saveGameFile.deleteOnExit();
        saveGameFile.delete();
    }

    @RequestMapping(path = "{gameId}/disable-validation", method = RequestMethod.POST)
    public GameOutputDTO disableValidation(@PathVariable String gameId) {
        return gameApplicationService.disableValidation(gameId);
    }
}
