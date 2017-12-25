package me.cybulski.civ5pbemserver.game.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Michał Cybulski
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CannotStartGameException extends RuntimeException {

    public CannotStartGameException(String message) {
        super(message);
    }
}
