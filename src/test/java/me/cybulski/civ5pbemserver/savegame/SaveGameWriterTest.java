package me.cybulski.civ5pbemserver.savegame;

import com.google.common.collect.ImmutableList;
import me.cybulski.civ5pbemserver.savegame.dto.SaveGamePlayerDTO;
import me.cybulski.civ5pbemserver.savegame.dto.SaveGamePlayerStatus;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author Michał Cybulski
 */
public class SaveGameWriterTest {

    private static final String SAVE_WRITER_ROOT = "save-writer/";

    private SaveGameWriter saveGameWriter = new SaveGameWriter(new SaveGameHelper());

    @Test
    public void blocksAreReplacedAsExpected() throws IOException {
        // given
        File fileSrc = getSaveFileInputStream("fake-save-game.bin");
        File expectedSrc = getSaveFileInputStream("expected-dest-fake-save-game.bin");
        File fileDest = File.createTempFile("fake-save-game", ".bin.tmp");

        // and
        byte[] bytesToReplace = new byte[] { 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11 };
        SaveGameWriter.ByteBlock byteBlock1 = new SaveGameWriter.ByteBlock(0, bytesToReplace);

        // and
        SaveGameWriter.ByteBlock byteBlock2 = new SaveGameWriter.ByteBlock(2, new byte[]{});

        // when
        saveGameWriter.replaceBlocks(fileSrc, fileDest, ImmutableList.of(byteBlock1, byteBlock2));

        // then
        assertThat(FileUtils.readFileToByteArray(fileDest)).isEqualTo(FileUtils.readFileToByteArray(expectedSrc));
    }

    private File getSaveFileInputStream(String fileName) {
        return new File(this.getClass().getClassLoader().getResource(SAVE_WRITER_ROOT + fileName).getFile());
    }

    @Test
    public void playerNamesAreGeneratedWell() throws IOException {
        // given
        List<SaveGamePlayerDTO> players = ImmutableList.of(
                SaveGamePlayerDTO.builder()
                        .playerNumber(0)
                        .playerName("Player 1")
                        .playerStatus(SaveGamePlayerStatus.HUMAN)
                        .build(),
                SaveGamePlayerDTO.builder()
                        .playerNumber(1)
                        .playerName("Player 2")
                        .playerStatus(SaveGamePlayerStatus.HUMAN)
                        .build(),
                SaveGamePlayerDTO.builder()
                        .playerNumber(2)
                        .playerName("Player 3")
                        .playerStatus(SaveGamePlayerStatus.HUMAN)
                        .build(),
                SaveGamePlayerDTO.builder()
                        .playerNumber(3)
                        .playerStatus(SaveGamePlayerStatus.AI)
                        .build()
        );

        // when
        List<SaveGameWriter.ByteBlock> byteBlocks = saveGameWriter.preparePlayerNameByteBlocks(players);

        // then
        assertThat(byteBlocks.size()).isEqualTo(2);

        // and
        assertThat(byteBlocks.get(0).getBlockIndex()).isEqualTo(1);
        byte[] expectedFirstPlayerBlock = FileUtils.readFileToByteArray(getSaveFileInputStream("player-block.bin"));
        assertThat(byteBlocks.get(0).getBytes())
                .isEqualTo(expectedFirstPlayerBlock);

        // and
        byte[] expectedSecondPlayerBlock = FileUtils.readFileToByteArray(getSaveFileInputStream("player-block-2.bin"));
        assertThat(byteBlocks.get(1).getBlockIndex()).isEqualTo(21);
        assertThat(byteBlocks.get(1).getBytes())
                .isEqualTo(expectedSecondPlayerBlock);
    }
}