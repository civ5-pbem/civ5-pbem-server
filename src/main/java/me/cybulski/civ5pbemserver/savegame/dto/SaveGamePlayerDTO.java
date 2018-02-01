package me.cybulski.civ5pbemserver.savegame.dto;

import lombok.*;

/**
 * @author Michał Cybulski
 */
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
public class SaveGamePlayerDTO {

    /**
     * Indexed from 0.
     */
    private int playerNumber;
    private String password;
    private SaveGamePlayerStatus playerStatus;
    private String playerName;
}
