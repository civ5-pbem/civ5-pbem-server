package me.cybulski.civ5pbemserver.game.dto;

import lombok.*;
import me.cybulski.civ5pbemserver.game.PlayerType;

import javax.validation.constraints.NotNull;

/**
 * @author Michał Cybulski
 */
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ChangePlayerTypeInputDTO {
    @NotNull
    private PlayerType playerType;
}
