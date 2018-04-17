package me.cybulski.civ5pbemserver.savegame.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Michał Cybulski
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CannotParseSaveGameException extends RuntimeException {

    public CannotParseSaveGameException(String message) {
        super(message);
    }

    public CannotParseSaveGameException(String message, Exception e) {
        super(message, e);
    }
}
