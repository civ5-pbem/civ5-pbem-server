package me.cybulski.civ5pbemserver.game.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Michał Cybulski
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class NoPermissionToModifyGameException extends RuntimeException {

    public NoPermissionToModifyGameException(String message) {
        super(message);
    }
}
