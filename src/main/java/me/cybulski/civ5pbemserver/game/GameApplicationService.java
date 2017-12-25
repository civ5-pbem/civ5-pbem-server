package me.cybulski.civ5pbemserver.game;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.cybulski.civ5pbemserver.exception.ResourceNotFoundException;
import me.cybulski.civ5pbemserver.game.dto.GameOutputDTO;
import me.cybulski.civ5pbemserver.game.dto.NewGameInputDTO;
import me.cybulski.civ5pbemserver.game.dto.PlayerOutputDTO;
import me.cybulski.civ5pbemserver.user.UserAccount;
import me.cybulski.civ5pbemserver.user.UserAccountApplicationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static me.cybulski.civ5pbemserver.config.SecurityConstants.HAS_ROLE_USER;

/**
 * @author Michał Cybulski
 */
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class GameApplicationService {

    private final UserAccountApplicationService userAccountApplicationService;
    private final GameFactory gameFactory;
    private final GameRepository gameRepository;

    @PreAuthorize(HAS_ROLE_USER)
    @Transactional
    public GameOutputDTO createNewGame(NewGameInputDTO newGameInputDTO) {
        Game newGame = gameFactory.createNewGame(
                userAccountApplicationService.getCurrentUserAccount().orElseThrow(RuntimeException::new),
                newGameInputDTO);
        gameRepository.save(newGame);

        return convertToDTO(newGame);
    }

    @PreAuthorize(HAS_ROLE_USER)
    @Transactional(readOnly = true)
    public GameOutputDTO findGameById(String gameId) {
        return convertToDTO(gameRepository.findById(gameId).orElseThrow(ResourceNotFoundException::new));
    }

    @PreAuthorize(HAS_ROLE_USER)
    @Transactional(readOnly = true)
    public List<GameOutputDTO> findAllGames() {
        return gameRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @PreAuthorize(HAS_ROLE_USER)
    @Transactional
    public GameOutputDTO joinGame(String gameId) {
        Game game = gameRepository.findById(gameId).orElseThrow(ResourceNotFoundException::new);
        UserAccount currentUser = userAccountApplicationService.getCurrentUserAccount()
                .orElseThrow(ResourceNotFoundException::new);
        game.joinGame(currentUser);

        return convertToDTO(game);
    }

    private GameOutputDTO convertToDTO(Game game) {
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
