package me.cybulski.civ5pbemserver.game;

import lombok.NonNull;
import me.cybulski.civ5pbemserver.game.dto.GameOutputDTO;
import me.cybulski.civ5pbemserver.game.dto.PlayerOutputDTO;
import me.cybulski.civ5pbemserver.user.UserAccount;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.stream.Collectors;

@Component
class GameToGameDTOConverter implements Converter<Game, GameOutputDTO> {

    @Override
    public GameOutputDTO convert(@NonNull Game game) {
        return GameOutputDTO
                .builder()
                .id(game.getId())
                .name(game.getName())
                .description(game.getDescription())
                .gameState(game.getGameState())
                .host(game.getHost().getUsername())
                .mapSize(game.getMapSize())
                .numberOfCityStates(game.getNumberOfCityStates())
                .players(game.getPlayers()
                                 .stream()
                                 .map(this::convertToDTO)
                                 .sorted(Comparator.comparingInt(PlayerOutputDTO::getPlayerNumber))
                                 .collect(Collectors.toList()))
                .currentlyMovingPlayer(game.getCurrentGameTurn()
                                               .map(GameTurn::getCurrentPlayer)
                                               .map(Player::getHumanUserAccount)
                                               .map(UserAccount::getUsername)
                                               .orElse(null))
                .lastMoveFinished(game.getCurrentGameTurn()
                                          .map(GameTurn::getCreatedAt)
                                          .orElse(null))
                .isSaveGameValidationEnabled(game.getShouldSaveGameFilesBeValidated())
                .build();
    }

    private PlayerOutputDTO convertToDTO(Player player) {
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
