package me.cybulski.civ5pbemserver.game;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.cybulski.civ5pbemserver.exception.ResourceNotFoundException;
import me.cybulski.civ5pbemserver.game.exception.InvalidSaveGameException;
import me.cybulski.civ5pbemserver.savegame.dto.SaveGameDTO;
import me.cybulski.civ5pbemserver.savegame.SaveGameParser;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * @author Michał Cybulski
 */
@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class SaveGameValidator {

    private final SaveGameRepository saveGameRepository;
    private final SaveGameParser saveGameParser;

    public void validateCurrentSaveFile(Game game) throws IOException {
        GameTurn gameTurn = game.getCurrentGameTurn().orElseThrow(ResourceNotFoundException::new);
        File file = saveGameRepository.loadFile(game, gameTurn.getSaveFilename()).getFile();
        SaveGameDTO parsedData = saveGameParser.parse(file);

        int calculatedPlayerNumber = gameTurn.getCurrentPlayer().getPlayerNumber();
        int saveGamePlayerNumber = parsedData.getPlayerWhoMoves().getPlayerNumber() + 1;
        if (calculatedPlayerNumber != saveGamePlayerNumber) {
            throw new InvalidSaveGameException("Wrong player's turn. Should be "
                                                       + calculatedPlayerNumber
                                                       + " but is "
                                                       + saveGamePlayerNumber);
        }

        int calculatedTurnNumber = gameTurn.getTurnNumber();
        int saveGameTurnNumber = parsedData.getTurnNumber();
        if (calculatedTurnNumber != saveGameTurnNumber) {
            throw new InvalidSaveGameException("Wrong turn number. Should be "
                                                       + calculatedTurnNumber
                                                       + " but is "
                                                       + saveGameTurnNumber);
        }
    }
}
