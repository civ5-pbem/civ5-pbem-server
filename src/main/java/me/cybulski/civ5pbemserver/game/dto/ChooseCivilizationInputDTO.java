package me.cybulski.civ5pbemserver.game.dto;

import lombok.*;
import me.cybulski.civ5pbemserver.game.Civilization;

/**
 * @author Michał Cybulski
 */
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ChooseCivilizationInputDTO {

    private Civilization civilization;
}
