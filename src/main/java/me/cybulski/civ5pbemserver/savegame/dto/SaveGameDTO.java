package me.cybulski.civ5pbemserver.savegame.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * @author Michał Cybulski
 */
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SaveGameDTO {

    private String gameVersion;
    private int turnNumber;
    private SaveGamePlayerDTO playerWhoMoves;
    private List<SaveGamePlayerDTO> players;
}
