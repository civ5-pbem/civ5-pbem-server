package me.cybulski.civ5pbemserver.game;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.cybulski.civ5pbemserver.game.dto.GameOutputDTO;
import me.cybulski.civ5pbemserver.game.dto.PlayerOutputDTO;
import me.cybulski.civ5pbemserver.user.UserAccount;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class GameToGameDTOConverter implements Converter<Game, GameOutputDTO> {

    private final Converter<Player, PlayerOutputDTO> playerToPlayerOutputDTOConverter;

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
                                 .map(playerToPlayerOutputDTOConverter::convert)
                                 .filter(Objects::nonNull)
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
                .turnNumber(game.getCurrentGameTurn().map(GameTurn::getTurnNumber).orElse(null))
                .build();
    }
}
