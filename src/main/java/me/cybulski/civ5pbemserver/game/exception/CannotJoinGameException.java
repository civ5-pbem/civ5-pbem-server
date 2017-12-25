package me.cybulski.civ5pbemserver.game.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Michał Cybulski
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CannotJoinGameException extends RuntimeException {

    public CannotJoinGameException(String message) {
        super(message);
    }
}
