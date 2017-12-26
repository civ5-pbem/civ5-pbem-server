package me.cybulski.civ5pbemserver.saveparser;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michał Cybulski
 */
@Component
public class SaveGameParser {

    private static final Charset CHARSET = Charset.forName("UTF-8");
    private static final int BLOCK_MARKER = 0x40000000;

    public SaveGameDTO parse(File file) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
        List<Long> allBlocks = findAllBlocks(randomAccessFile);

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

        return SaveGameDTO.builder()
                .gameVersion(gameVersion)
                .turnNumber(turnNumber)
                .playerWhoMoves(playerWhoMoves)
                .players(players)
                .build();
    }

    private void readPlayerPasswords(RandomAccessFile randomAccessFile, List<SaveGamePlayerDTO> players) throws IOException {
        for (SaveGamePlayerDTO player : players) {
            player.setPassword(readString(randomAccessFile));
        }
    }

    private List<SaveGamePlayerDTO> readPlayerStatuses(RandomAccessFile randomAccessFile) throws IOException {
        List<SaveGamePlayerDTO> result = new ArrayList<>();

        // 22 is the max player count
        for (int i = 0; i < 22; i++) {
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
     * Finds next block marked by '0x40000000'.
     */
    private List<Long> findAllBlocks(RandomAccessFile randomAccessFile) throws IOException {
        List<Long> result = new ArrayList<>();
        long filePointerAtTheBeginning = randomAccessFile.getFilePointer();

        randomAccessFile.seek(0);

        byte[] intBytes = new byte[4];
        long filePointer;
        long read;
        while (true) {
            filePointer = randomAccessFile.getFilePointer();
            read = randomAccessFile.read(intBytes);

            if (wrapBytes(intBytes).order(ByteOrder.BIG_ENDIAN).getInt() == BLOCK_MARKER) {
                result.add(filePointer);
            }

            if (read < 4) {
                break;
            }

            randomAccessFile.seek(filePointer);
            randomAccessFile.skipBytes(1);
        }

        randomAccessFile.seek(filePointerAtTheBeginning);
        return result;
    }

    private ByteBuffer wrapBytes(byte[] bytes) {
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
    }

    /**
     * Read a string with the length given in the first 4 bytes.
     */
    private String readString(RandomAccessFile randomAccessFile) throws IOException {
        byte[] lengthBytes = new byte[4];
        Assert.state(randomAccessFile.read(lengthBytes) == 4, "Couldn't read 4 bytes");
        int length = wrapBytes(lengthBytes).getInt();

        byte[] stringBytes = new byte[length];
        Assert.state(randomAccessFile.read(stringBytes) == length, "Couldn't read " + length + " bytes");

        return new String(stringBytes, CHARSET);
    }

    private int readInt(RandomAccessFile randomAccessFile) throws IOException {
        byte[] lengthBytes = new byte[4];
        Assert.state(randomAccessFile.read(lengthBytes) == 4, "Couldn't read 4 bytes");

        return wrapBytes(lengthBytes).getInt();
    }
}
