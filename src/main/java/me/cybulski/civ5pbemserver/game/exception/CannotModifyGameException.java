package me.cybulski.civ5pbemserver.game.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Michał Cybulski
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CannotModifyGameException extends RuntimeException {

    public CannotModifyGameException(String message) {
        super(message);
    }
}
