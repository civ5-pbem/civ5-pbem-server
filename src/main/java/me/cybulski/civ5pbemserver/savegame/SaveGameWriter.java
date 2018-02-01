package me.cybulski.civ5pbemserver.savegame;

import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import me.cybulski.civ5pbemserver.savegame.dto.SaveGamePlayerDTO;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Michał Cybulski
 */
@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class SaveGameWriter {

    private final SaveGameHelper saveGameHelper;

    public List<ByteBlock> preparePlayerNameByteBlocks(List<SaveGamePlayerDTO> players) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // write player names
        for (SaveGamePlayerDTO player : players) {
            byteArrayOutputStream.write(prepareString(player.getPlayerName()));
        }
        int missingPlayers = SaveGameHelper.MAX_PLAYER_COUNT - players.size();

        // write empty strings for missing players
        byte[] fourZeroes = ByteBuffer.allocate(4).putInt(0).array();
        for (int i = 0; i < missingPlayers; i++) {
            byteArrayOutputStream.write(fourZeroes);
        }

        // add padding with length of 0xA8
        for (int i = 0; i < 0xA8; i++) {
            byteArrayOutputStream.write(0);
        }
        ByteBlock firstBlock = new ByteBlock(1, byteArrayOutputStream.toByteArray());

        // second block is 8 bytes longer
        byteArrayOutputStream.write(fourZeroes);
        byteArrayOutputStream.write(fourZeroes);
        ByteBlock secondBlock = new ByteBlock(21, byteArrayOutputStream.toByteArray());

        return ImmutableList.of(firstBlock, secondBlock);
    }

    private byte[] prepareString(String string) throws IOException {
        if (string == null) {
            string = "";
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(4 + string.length());
        byteArrayOutputStream.write(saveGameHelper.allocateByteBuffer(4).putInt(string.length()).array());

        for (int i = 0; i < string.length(); i++) {
            byteArrayOutputStream.write(string.charAt(i));
        }

        return byteArrayOutputStream.toByteArray();
    }

    public void replaceBlocks(File sourceFile, File destinationFile, Collection<ByteBlock> blocks) throws IOException {
        try (
                RandomAccessFile randomAccessSourceFile = new RandomAccessFile(sourceFile, "r");
                RandomAccessFile randomAccessDestinationFile = new RandomAccessFile(destinationFile, "rw")
        ) {
            List<ByteBlock> sortedBlocks = blocks.stream()
                                                   .sorted(Comparator.comparingInt(ByteBlock::getBlockIndex))
                                                   .collect(Collectors.toList());
            List<Long> allBlocks = saveGameHelper.findAllBlocks(randomAccessSourceFile);

            for (ByteBlock byteBlock : sortedBlocks) {
                long blockLocation = allBlocks.get(byteBlock.getBlockIndex());
                long bytesToPump = blockLocation - randomAccessSourceFile.getFilePointer();

                pumpBytes(randomAccessSourceFile, randomAccessDestinationFile, bytesToPump);

                long bytesToSkip = allBlocks.get(byteBlock.getBlockIndex() + 1) - blockLocation;
                Assert.state(
                        randomAccessSourceFile.skipBytes((int) bytesToSkip) == bytesToSkip,
                        "Unexpected end of file");

                writeBlockMarker(randomAccessDestinationFile);
                randomAccessDestinationFile.write(byteBlock.getBytes());
            }

            long bytesUntilEndOfFile = randomAccessSourceFile.length() - randomAccessSourceFile.getFilePointer();
            pumpBytes(randomAccessSourceFile, randomAccessDestinationFile, bytesUntilEndOfFile);
        }
    }

    private void writeBlockMarker(RandomAccessFile randomAccessDestinationFile) throws IOException {
        randomAccessDestinationFile.write(ByteBuffer.allocate(4).putInt(SaveGameHelper.BLOCK_MARKER).array());
    }

    private void pumpBytes(RandomAccessFile randomAccessSourceFile,
                           RandomAccessFile randomAccessDestinationFile,
                           long bytesToPump)
            throws IOException {
        byte[] buffer = new byte[512];
        while (bytesToPump > 0) {
            long bytesRead = randomAccessSourceFile.read(buffer, 0, (int) Long.min(bytesToPump, buffer.length));
            Assert.state(bytesRead != -1, "Unexpected end of file!");
            bytesToPump -= bytesRead;
            randomAccessDestinationFile.write(buffer, 0, (int) bytesRead);
        }
    }

    @Value
    public static class ByteBlock {
        private int blockIndex;
        private byte[] bytes;
    }
}
