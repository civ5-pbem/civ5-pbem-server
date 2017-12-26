package me.cybulski.civ5pbemserver.civilization.dto;

import lombok.*;
import me.cybulski.civ5pbemserver.civilization.Civilization;

/**
 * @author Krzysztof Cybulski
 */
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CivilizationDTO {

    private String code;
    private String name;
    private String leader;
}
