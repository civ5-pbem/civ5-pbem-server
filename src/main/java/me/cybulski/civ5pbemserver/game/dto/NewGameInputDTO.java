package me.cybulski.civ5pbemserver.game.dto;

import lombok.*;
import me.cybulski.civ5pbemserver.game.MapSize;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author Michał Cybulski
 */
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class NewGameInputDTO {

    @NotNull
    private String gameName;
    @Size(max = 256)
    private String gameDescription;
    @NotNull
    private MapSize mapSize;

}
