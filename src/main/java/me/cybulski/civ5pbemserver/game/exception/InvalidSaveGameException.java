package me.cybulski.civ5pbemserver.game.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Michał Cybulski
 */
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class InvalidSaveGameException extends RuntimeException {

    public InvalidSaveGameException(String message) {
        super(message);
    }
}
