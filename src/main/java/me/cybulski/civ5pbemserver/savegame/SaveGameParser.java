package me.cybulski.civ5pbemserver.savegame;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.cybulski.civ5pbemserver.savegame.dto.SaveGameDTO;
import me.cybulski.civ5pbemserver.savegame.dto.SaveGamePlayerDTO;
import me.cybulski.civ5pbemserver.savegame.dto.SaveGamePlayerStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michał Cybulski
 */
@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class SaveGameParser {

    private static final Charset CHARSET = Charset.forName("UTF-8");

    private final SaveGameHelper saveGameHelper;

    public SaveGameDTO parse(File file) throws IOException {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            List<Long> allBlocks = saveGameHelper.findAllBlocks(randomAccessFile);

            Assert.state(randomAccessFile.skipBytes(8) == 8, "Couldn't skip 8 bytes");

            // game metadata
            String gameVersion = readString(randomAccessFile);
            readString(randomAccessFile);
            int turnNumber = readInt(randomAccessFile);

            // player statuses
            randomAccessFile.seek(allBlocks.get(2) + 4);
            List<SaveGamePlayerDTO> players = readPlayerStatuses(randomAccessFile);

            // player who moves
            randomAccessFile.seek(allBlocks.get(8) - 16);
            SaveGamePlayerDTO playerWhoMoves = players.get(readInt(randomAccessFile));

            // player passwords
            randomAccessFile.seek(allBlocks.get(11) + 4);
            readPlayerPasswords(randomAccessFile, players);

            // player names
            randomAccessFile.seek(allBlocks.get(21) + 4);
            readPlayerNames(randomAccessFile, players);

            return SaveGameDTO.builder()
                    .gameVersion(gameVersion)
                    .turnNumber(turnNumber)
                    .playerWhoMoves(playerWhoMoves)
                    .players(players)
                    .build();
        }
    }

    private void readPlayerPasswords(RandomAccessFile randomAccessFile, List<SaveGamePlayerDTO> players) throws IOException {
        for (SaveGamePlayerDTO player : players) {
            player.setPassword(readString(randomAccessFile));
        }
    }

    private void readPlayerNames(RandomAccessFile randomAccessFile, List<SaveGamePlayerDTO> players) throws IOException {
        for (SaveGamePlayerDTO player : players) {
            player.setPlayerName(readString(randomAccessFile));
        }
    }

    private List<SaveGamePlayerDTO> readPlayerStatuses(RandomAccessFile randomAccessFile) throws IOException {
        List<SaveGamePlayerDTO> result = new ArrayList<>();

        // 22 is the max player count
        for (int i = 0; i < SaveGameHelper.MAX_PLAYER_COUNT; i++) {
            int playerStatusCode = readInt(randomAccessFile);
            SaveGamePlayerStatus playerStatus = SaveGamePlayerStatus.getForCode(playerStatusCode);
            if (SaveGamePlayerStatus.MISSING.equals(playerStatus)) {
                break;
            }

            result.add(SaveGamePlayerDTO.builder()
                               .playerNumber(i)
                               .playerStatus(playerStatus)
                               .build());
        }

        return result;
    }

    /**
     * Read a string with the length given in the first 4 bytes.
     */
    private String readString(RandomAccessFile randomAccessFile) throws IOException {
        byte[] lengthBytes = new byte[4];
        Assert.state(randomAccessFile.read(lengthBytes) == 4, "Couldn't read 4 bytes");
        int length = saveGameHelper.wrapBytesLittleEndian(lengthBytes).getInt();

        byte[] stringBytes = new byte[length];
        Assert.state(randomAccessFile.read(stringBytes) == length, "Couldn't read " + length + " bytes");

        return new String(stringBytes, CHARSET);
    }

    private int readInt(RandomAccessFile randomAccessFile) throws IOException {
        byte[] lengthBytes = new byte[4];
        Assert.state(randomAccessFile.read(lengthBytes) == 4, "Couldn't read 4 bytes");

        return saveGameHelper.wrapBytesLittleEndian(lengthBytes).getInt();
    }
}
