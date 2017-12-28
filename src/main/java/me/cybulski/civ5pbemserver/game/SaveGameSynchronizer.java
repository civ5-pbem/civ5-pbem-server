package me.cybulski.civ5pbemserver.game;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.cybulski.civ5pbemserver.exception.ResourceNotFoundException;
import me.cybulski.civ5pbemserver.saveparser.SaveGameDTO;
import me.cybulski.civ5pbemserver.saveparser.SaveGameParser;
import me.cybulski.civ5pbemserver.saveparser.SaveGamePlayerStatus;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * @author Michał Cybulski
 */
@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class SaveGameSynchronizer {

    private final SaveGameRepository saveGameRepository;
    private final SaveGameParser saveGameParser;

    void synchronizeDeadPlayers(Game game, String saveFilename) throws IOException {
        File file = saveGameRepository.loadFile(game, saveFilename).getFile();
        SaveGameDTO parsedData = saveGameParser.parse(file);

        parsedData.getPlayers().stream()
                .filter(saveGamePlayerDTO -> SaveGamePlayerStatus.DEAD.equals(saveGamePlayerDTO.getPlayerStatus()))
                .forEach(saveGamePlayerDTO -> game.getPlayerList().get(saveGamePlayerDTO.getPlayerNumber()).die());
    }

    void synchronizeTurnNumberAndCurrentPlayer(Game game, String saveFilename) throws IOException {
        File file = saveGameRepository.loadFile(game, saveFilename).getFile();
        SaveGameDTO parsedData = saveGameParser.parse(file);

        GameTurn gameTurn = game.getCurrentGameTurn().orElseThrow(ResourceNotFoundException::new);
        gameTurn.syncrhonizeWithSaveGame(
                parsedData.getTurnNumber(),
                game.getPlayerList().get(parsedData.getPlayerWhoMoves().getPlayerNumber()));
    }
}
