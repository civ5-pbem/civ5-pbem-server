package me.cybulski.civ5pbemserver.savegame.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * @author Michał Cybulski
 */
@Getter
@RequiredArgsConstructor
public enum SaveGamePlayerStatus {
    AI(1),
    DEAD(2),
    HUMAN(3),
    MISSING(4);

    private final int code;

    public static SaveGamePlayerStatus getForCode(int code) {
        return Arrays.stream(SaveGamePlayerStatus.values())
                .filter(status -> status.getCode() == code)
                .findFirst()
                .orElse(null);
    }
}
