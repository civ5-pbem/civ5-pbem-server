package me.cybulski.civ5pbemserver.game.dto;

import lombok.*;
import me.cybulski.civ5pbemserver.civilization.Civilization;
import me.cybulski.civ5pbemserver.game.PlayerType;

/**
 * @author Michał Cybulski
 */
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PlayerOutputDTO {

    private String id;
    private int playerNumber;
    private Civilization civilization;
    private PlayerType playerType;
    private String humanUserAccount;
}
