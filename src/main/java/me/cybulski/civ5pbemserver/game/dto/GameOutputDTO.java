package me.cybulski.civ5pbemserver.game.dto;

import lombok.*;
import me.cybulski.civ5pbemserver.game.GameState;
import me.cybulski.civ5pbemserver.game.MapSize;

import java.util.List;

/**
 * @author Michał Cybulski
 */
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class GameOutputDTO {

    private String id;
    private String host;
    private String name;
    private String description;
    private MapSize mapSize;
    private GameState gameState;
    private List<PlayerOutputDTO> players;
    private int numberOfCityStates;
}
