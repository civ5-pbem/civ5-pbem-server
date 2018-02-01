package me.cybulski.civ5pbemserver.savegame;

import me.cybulski.civ5pbemserver.savegame.dto.SaveGameDTO;
import me.cybulski.civ5pbemserver.savegame.dto.SaveGamePlayerDTO;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author Michał Cybulski
 */
public class SaveGameEndToEndTest {

    public static final String SAVE_GAMES_PATH = "save-games/";

    private SaveGameHelper saveGameHelper = new SaveGameHelper();
    private SaveGameParser saveGameParser = new SaveGameParser(saveGameHelper);
    private SaveGameWriter saveGameWriter = new SaveGameWriter(saveGameHelper);

    @Test
    public void writerCanChangePlayerNames() throws IOException {
        // given
        File sourceSaveFile = getSaveFileInputStream("after_26_turn.Civ5Save");
        SaveGameDTO parse = saveGameParser.parse(sourceSaveFile);

        // and
        String newPlayerName = "Test name!";
        SaveGamePlayerDTO firstPlayer = parse.getPlayers().get(0);
        assertThat(firstPlayer.getPlayerName()).isEqualTo("Player 1");
        firstPlayer.setPlayerName(newPlayerName);

        // when
        File resultFile = File.createTempFile("after_26_turn", ".Civ5Save.tmp");
        saveGameWriter.replaceBlocks(
                sourceSaveFile,
                resultFile,
                saveGameWriter.preparePlayerNameByteBlocks(parse.getPlayers()));

        // then
        SaveGameDTO secondParse = saveGameParser.parse(resultFile);
        assertThat(secondParse.getPlayers().get(0).getPlayerName()).isEqualTo(newPlayerName);
    }

    private File getSaveFileInputStream(String fileName) {
        return new File(this.getClass().getClassLoader().getResource(SAVE_GAMES_PATH + fileName).getFile());
    }
}