package me.cybulski.civ5pbemserver.savegame;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michał Cybulski
 */
@Component
public class SaveGameHelper {

    public static final int MAX_PLAYER_COUNT = 22;

    public static final int BLOCK_MARKER = 0x40000000;

    /**
     * Finds next block marked by '0x40000000'.
     */
    List<Long> findAllBlocks(RandomAccessFile randomAccessFile) throws IOException {
        List<Long> result = new ArrayList<>();
        long filePointerAtTheBeginning = randomAccessFile.getFilePointer();

        randomAccessFile.seek(0);

        byte[] intBytes = new byte[4];
        long filePointer;
        long read;
        while (true) {
            filePointer = randomAccessFile.getFilePointer();
            read = randomAccessFile.read(intBytes);

            if (wrapBytesLittleEndian(intBytes).order(ByteOrder.BIG_ENDIAN).getInt() == BLOCK_MARKER) {
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

    ByteBuffer wrapBytesLittleEndian(byte[] bytes) {
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
    }

    ByteBuffer allocateByteBuffer(int capacity) {
        return ByteBuffer.allocate(capacity).order(ByteOrder.LITTLE_ENDIAN);
    }
}
