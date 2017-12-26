package me.cybulski.civ5pbemserver.game;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @author Michał Cybulski
 */
@Repository
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class SaveGameRepository {

    private final Environment environment;
    private String saveGameFilePathRoot;

    @PostConstruct
    public void init() {
        saveGameFilePathRoot = environment.getProperty("civ5.saves-dir");
    }

    public String saveFile(Game game, MultipartFile multipartFile) {
        String filename = generateFileName();
        File file = createFile(getGameDirectory(game), filename);
        try {
            multipartFile.transferTo(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return filename;
    }

    public Resource loadFile(GameTurn gameTurn) {
        return new FileSystemResource(createFile(getGameDirectory(gameTurn.getGame()), gameTurn.getSaveFilename()));
    }

    private File createFile(File gameRootDir, String fileName) {
        return new File(gameRootDir, fileName);
    }

    private String generateFileName() {
        return UUID.randomUUID().toString() + ".save";
    }

    private File getGameDirectory(Game game) {
        File potentialGameDirectory = new File(saveGameFilePathRoot, game.getId());
        if (!potentialGameDirectory.isDirectory() && !potentialGameDirectory.mkdirs()) {
            throw new RuntimeException("Cannot make a directory: " + potentialGameDirectory);
        }

        return potentialGameDirectory;
    }
}
