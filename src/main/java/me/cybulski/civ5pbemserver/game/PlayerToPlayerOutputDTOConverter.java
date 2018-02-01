package me.cybulski.civ5pbemserver.game;

import lombok.NonNull;
import me.cybulski.civ5pbemserver.game.dto.PlayerOutputDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * @author Michał Cybulski
 */
@Component
class PlayerToPlayerOutputDTOConverter implements Converter<Player, PlayerOutputDTO> {

    @Override
    public PlayerOutputDTO convert(@NonNull Player player) {
        return PlayerOutputDTO.builder()
                       .id(player.getId())
                       .playerNumber(player.getPlayerNumber())
                       .civilization(player.getCivilization())
                       .humanUserAccount(player.getHumanUserAccount() != null
                                                 ? player.getHumanUserAccount().getUsername()
                                                 : null)
                       .playerType(player.getPlayerType())
                       .build();
    }
}
