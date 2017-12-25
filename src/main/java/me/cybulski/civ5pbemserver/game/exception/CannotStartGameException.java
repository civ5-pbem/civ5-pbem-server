package me.cybulski.civ5pbemserver.game.exception;

/**
 * @author Michał Cybulski
 */
public class CannotStartGameException extends RuntimeException {

    public CannotStartGameException(String message) {
        super(message);
    }
}
