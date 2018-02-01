package me.cybulski.civ5pbemserver.game;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.cybulski.civ5pbemserver.savegame.SaveGameParser;
import me.cybulski.civ5pbemserver.savegame.SaveGameWriter;
import me.cybulski.civ5pbemserver.savegame.dto.SaveGameDTO;
import me.cybulski.civ5pbemserver.savegame.dto.SaveGamePlayerDTO;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Michał Cybulski
 */
@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DynamicSaveGameGenerator {

    private final SaveGameParser saveGameParser;
    private final SaveGameWriter saveGameWriter;

    public Resource generateSaveGameForNextPlayer(GameTurn gameTurn, File originalSaveGame) throws IOException {
        SaveGameDTO originalSaveGameDTO = saveGameParser.parse(originalSaveGame);
        List<Player> gamePlayerList = gameTurn.getGame().getPlayerList();
        List<SaveGamePlayerDTO> playerDTOs = originalSaveGameDTO.getPlayers();

        // generating player names
        for (SaveGamePlayerDTO saveGamePlayerDTO : playerDTOs) {
            Player player = gamePlayerList.get(saveGamePlayerDTO.getPlayerNumber());
            if (PlayerType.HUMAN.equals(player.getPlayerType())) {
                saveGamePlayerDTO.setPlayerName(player.getHumanUserAccount().getUsername());
            } else {
                saveGamePlayerDTO.setPlayerName("");
            }
        }
        List<SaveGameWriter.ByteBlock> playerByteBlocks = saveGameWriter.preparePlayerNameByteBlocks(playerDTOs);

        File temporaryDestinationFile = File.createTempFile("civ5temp", "tmp");
        saveGameWriter.replaceBlocks(originalSaveGame, temporaryDestinationFile, playerByteBlocks);

        return new FileSystemResource(temporaryDestinationFile);
    }
}
