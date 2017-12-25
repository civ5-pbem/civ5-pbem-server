package me.cybulski.civ5pbemserver.game.exception;

/**
 * @author Michał Cybulski
 */
public class CannotJoinGameException extends RuntimeException {

    public CannotJoinGameException(String message) {
        super(message);
    }
}
