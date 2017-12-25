package me.cybulski.civ5pbemserver.game;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.cybulski.civ5pbemserver.exception.ResourceNotFoundException;
import me.cybulski.civ5pbemserver.game.dto.*;
import me.cybulski.civ5pbemserver.game.exception.NoPermissionToModifyGameException;
import me.cybulski.civ5pbemserver.user.UserAccount;
import me.cybulski.civ5pbemserver.user.UserAccountApplicationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

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
    public GameOutputDTO changePlayerType(String gameId, String playerId, ChangePlayerTypeInputDTO changePlayerTypeInputDTO) {
        Game game = gameRepository.findById(gameId).orElseThrow(ResourceNotFoundException::new);
        UserAccount currentUserAccount = userAccountApplicationService.getCurrentUserAccount()
                .orElseThrow(ResourceNotFoundException::new);
        Assert.state(currentUserAccount.equals(game.getHost()), "Only host can change this setting!");

        game.changePlayerType(playerId, changePlayerTypeInputDTO.getPlayerType());

        return convertToDTO(game);
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

    @PreAuthorize(HAS_ROLE_USER)
    @Transactional
    public GameOutputDTO chooseCivilization(String gameId, String playerId, ChooseCivilizationInputDTO chooseCivilizationInputDTO) {
        Game game = gameRepository.findById(gameId).orElseThrow(ResourceNotFoundException::new);
        UserAccount currentUser = userAccountApplicationService.getCurrentUserAccount()
                .orElseThrow(ResourceNotFoundException::new);
        if (!checkCanModifyPlayer(game, playerId, currentUser)) {
            throw new NoPermissionToModifyGameException("Cannot modify player - no permission!");
        }

        game.chooseCivilization(playerId, chooseCivilizationInputDTO.getCivilization());

        return convertToDTO(game);
    }

    @PreAuthorize(HAS_ROLE_USER)
    @Transactional
    public GameOutputDTO kickPlayer(String gameId, String playerId) {
        Game game = gameRepository.findById(gameId).orElseThrow(ResourceNotFoundException::new);
        UserAccount currentUser = userAccountApplicationService.getCurrentUserAccount()
                .orElseThrow(ResourceNotFoundException::new);
        if (!game.getHost().equals(currentUser)) {
            throw new NoPermissionToModifyGameException("Cannot kick player - no permission!");
        }
        // FIXME #8
        // FIXME #9
        game.kickPlayer(playerId);

        return convertToDTO(game);
    }

    @PreAuthorize(HAS_ROLE_USER)
    @Transactional
    public GameOutputDTO leaveGame(String gameId) {
        // FIXME #9
        Game game = gameRepository.findById(gameId).orElseThrow(ResourceNotFoundException::new);
        UserAccount currentUser = userAccountApplicationService.getCurrentUserAccount()
                .orElseThrow(ResourceNotFoundException::new);
        game.leave(currentUser);

        return convertToDTO(game);
    }

    @PreAuthorize(HAS_ROLE_USER)
    @Transactional
    public GameOutputDTO startGame(String gameId) {
        Game game = gameRepository.findById(gameId).orElseThrow(ResourceNotFoundException::new);
        UserAccount currentUser = userAccountApplicationService.getCurrentUserAccount()
                .orElseThrow(ResourceNotFoundException::new);
        if (!game.getHost().equals(currentUser)) {
            throw new NoPermissionToModifyGameException("Only host can start the game!");
        }
        game.startGame();

        return convertToDTO(game);
    }

    private boolean checkCanModifyPlayer(Game game, String playerId, UserAccount userAccount) {
        Player foundPlayer = game.getPlayers().stream()
                .filter(player -> player.getId().equals(playerId))
                .findFirst()
                .orElseThrow(ResourceNotFoundException::new);

        return (game.getHost().equals(userAccount) && PlayerType.AI.equals(foundPlayer.getPlayerType()))
                || userAccount.equals(foundPlayer.getHumanUserAccount());
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
