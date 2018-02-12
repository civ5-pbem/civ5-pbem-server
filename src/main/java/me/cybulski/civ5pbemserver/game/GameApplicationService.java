package me.cybulski.civ5pbemserver.game;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.cybulski.civ5pbemserver.exception.ResourceNotFoundException;
import me.cybulski.civ5pbemserver.game.dto.ChangePlayerTypeInputDTO;
import me.cybulski.civ5pbemserver.game.dto.ChooseCivilizationInputDTO;
import me.cybulski.civ5pbemserver.game.dto.GameOutputDTO;
import me.cybulski.civ5pbemserver.game.dto.NewGameInputDTO;
import me.cybulski.civ5pbemserver.game.exception.NoPermissionToModifyGameException;
import me.cybulski.civ5pbemserver.jpa.BaseEntity;
import me.cybulski.civ5pbemserver.mail.MailService;
import me.cybulski.civ5pbemserver.user.UserAccount;
import me.cybulski.civ5pbemserver.user.UserAccountApplicationService;
import org.apache.commons.io.IOUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
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
    private final GameTurnFactory gameTurnFactory;
    private final SaveGameRepository saveGameRepository;
    private final Converter<Game, GameOutputDTO> gameToGameDTOConverter;
    private final CurrentGameTurnValidator currentGameTurnValidator;
    private final SaveGameValidator saveGameValidator;
    private final SaveGameSynchronizer saveGameSynchronizer;
    private final DynamicSaveGameGenerator dynamicSaveGameGenerator;
    private final MailService mailService;

    @PreAuthorize(HAS_ROLE_USER)
    @Transactional
    public GameOutputDTO createNewGame(NewGameInputDTO newGameInputDTO) {
        Game newGame = gameFactory.createNewGame(
                userAccountApplicationService.getCurrentUserAccount().orElseThrow(RuntimeException::new),
                newGameInputDTO);

        return gameToGameDTOConverter.convert(newGame);
    }

    @PreAuthorize(HAS_ROLE_USER)
    @Transactional(readOnly = true)
    public GameOutputDTO findGameById(String gameId) {
        return gameToGameDTOConverter.convert(findGameOrThrow(gameId));
    }

    @PreAuthorize(HAS_ROLE_USER)
    @Transactional(readOnly = true)
    public List<GameOutputDTO> findAllGames() {
        return gameRepository.findAll().stream()
                .sorted(Comparator.comparing(BaseEntity::getCreatedAt))
                .map(gameToGameDTOConverter::convert)
                .collect(Collectors.toList());
    }

    @PreAuthorize(HAS_ROLE_USER)
    @Transactional
    public GameOutputDTO changePlayerType(String gameId, String playerId, ChangePlayerTypeInputDTO changePlayerTypeInputDTO) {
        Game game = findGameOrThrow(gameId);
        UserAccount currentUserAccount = getCurrentUserOrThrow();
        Assert.state(currentUserAccount.equals(game.getHost()), "Only host can change this setting!");

        game.changePlayerType(playerId, changePlayerTypeInputDTO.getPlayerType());

        return gameToGameDTOConverter.convert(game);
    }

    @PreAuthorize(HAS_ROLE_USER)
    @Transactional
    public GameOutputDTO joinGame(String gameId) {
        Game game = findGameOrThrow(gameId);
        UserAccount currentUser = getCurrentUserOrThrow();

        game.joinGame(currentUser);

        return gameToGameDTOConverter.convert(game);
    }

    @PreAuthorize(HAS_ROLE_USER)
    @Transactional
    public GameOutputDTO chooseCivilization(String gameId, String playerId, ChooseCivilizationInputDTO chooseCivilizationInputDTO) {
        Game game = findGameOrThrow(gameId);
        UserAccount currentUser = getCurrentUserOrThrow();
        if (!checkCanModifyPlayer(game, playerId, currentUser)) {
            throw new NoPermissionToModifyGameException("Cannot modify player - no permission!");
        }

        game.chooseCivilization(playerId, chooseCivilizationInputDTO.getCivilization());

        return gameToGameDTOConverter.convert(game);
    }

    @PreAuthorize(HAS_ROLE_USER)
    @Transactional
    public GameOutputDTO kickPlayer(String gameId, String playerId) {
        Game game = findGameOrThrow(gameId);
        UserAccount currentUser = getCurrentUserOrThrow();
        if (!game.getHost().equals(currentUser)) {
            throw new NoPermissionToModifyGameException("Cannot kick player - no permission!");
        }

        UserAccount humanUserAccount = game.findPlayer(playerId).get().getHumanUserAccount();
        game.kickPlayer(playerId);
        mailService.sendYouWereKickedEmail(humanUserAccount.getEmail(), game.getName());

        return gameToGameDTOConverter.convert(game);
    }

    @PreAuthorize(HAS_ROLE_USER)
    @Transactional
    public GameOutputDTO leaveGame(String gameId) {
        Game game = findGameOrThrow(gameId);
        UserAccount currentUser = getCurrentUserOrThrow();

        game.leave(currentUser);

        return gameToGameDTOConverter.convert(game);
    }

    @PreAuthorize(HAS_ROLE_USER)
    @Transactional
    public GameOutputDTO startGame(String gameId) {
        Game game = findGameOrThrow(gameId);
        UserAccount currentUser = getCurrentUserOrThrow();
        if (!game.getHost().equals(currentUser)) {
            throw new NoPermissionToModifyGameException("Only host can start the game!");
        }

        game.startGame();
        GameTurn firstGameTurn = gameTurnFactory.createFirstGameTurn(game);
        game.nextTurn(firstGameTurn);
        Consumer<String> emailFunction = email -> mailService.sendGameJustStarted(email, game.getName());
        sendEmailToAllHumanPlayers(game, emailFunction);

        return gameToGameDTOConverter.convert(game);
    }

    private UserAccount getCurrentUserOrThrow() {
        return userAccountApplicationService.getCurrentUserAccount()
                .orElseThrow(ResourceNotFoundException::new);
    }

    @PreAuthorize(HAS_ROLE_USER)
    @Transactional
    public GameOutputDTO finishTurn(String gameId, MultipartFile multipartFile) throws IOException {
        Game game = findGameOrThrow(gameId);
        UserAccount currentUser = getCurrentUserOrThrow();
        currentGameTurnValidator.checkCurrentTurnOrThrow(game, currentUser);

        boolean validateSaveGame = game.getShouldSaveGameFilesBeValidated();
        GameTurn currentGameTurn = game.getCurrentGameTurn().orElseThrow(ResourceNotFoundException::new);
        String savedFileName = saveGameRepository.saveFile(game, multipartFile);

        saveGameSynchronizer.synchronizeDeadPlayers(game, savedFileName);

        GameTurn nextTurn = gameTurnFactory.createNextTurn(currentGameTurn, game.getPlayerList(), savedFileName);
        game.nextTurn(nextTurn);

        if (validateSaveGame) {
            saveGameValidator.validateCurrentSaveFile(game);
        } else {
            saveGameSynchronizer.synchronizeTurnNumberAndCurrentPlayer(game, savedFileName);
        }
        mailService.sendYourTurnEmail(nextTurn.getCurrentPlayer().getHumanUserAccount().getEmail(), game.getName());

        return gameToGameDTOConverter.convert(game);
    }

    @PreAuthorize(HAS_ROLE_USER)
    @Transactional
    public long writeDynamicSaveGameForTurn(String gameId, OutputStream targetOutputStream) throws IOException {
        Game game = findGameOrThrow(gameId);
        UserAccount currentUser = getCurrentUserOrThrow();
        currentGameTurnValidator.checkCurrentTurnOrThrow(game, currentUser);

        GameTurn gameTurn = game.getCurrentGameTurn().get();
        Resource originalSaveGame = saveGameRepository.loadFile(game, gameTurn.getSaveFilename());
        Resource dynamicSaveGame = dynamicSaveGameGenerator.generateSaveGameForNextPlayer(
                gameTurn,
                originalSaveGame.getFile());

        InputStream inputStream = dynamicSaveGame.getInputStream();
        long fileSize = IOUtils.copy(inputStream, targetOutputStream);
        inputStream.close();
        targetOutputStream.close();

        dynamicSaveGame.getFile().deleteOnExit();
        dynamicSaveGame.getFile().delete();

        return fileSize;
    }

    @PreAuthorize(HAS_ROLE_USER)
    @Transactional
    public GameOutputDTO disableValidation(String gameId) {
        Game game = findGameOrThrow(gameId);
        UserAccount currentUser = getCurrentUserOrThrow();
        if (!game.getHost().equals(currentUser)) {
            throw new NoPermissionToModifyGameException("Only host can start the game!");
        }
        game.disableValidation();
        Consumer<String> emailFunction = email -> mailService.sendSaveGameValidationDisabledEmail(email, game.getName());
        sendEmailToAllHumanPlayers(game, emailFunction);

        return gameToGameDTOConverter.convert(game);
    }

    private Game findGameOrThrow(String gameId) {
        return gameRepository.findById(gameId).orElseThrow(ResourceNotFoundException::new);
    }

    private boolean checkCanModifyPlayer(Game game, String playerId, UserAccount userAccount) {
        Player foundPlayer = game.getPlayers().stream()
                .filter(player -> player.getId().equals(playerId))
                .findFirst()
                .orElseThrow(ResourceNotFoundException::new);

        return (game.getHost().equals(userAccount) && PlayerType.AI.equals(foundPlayer.getPlayerType()))
                || userAccount.equals(foundPlayer.getHumanUserAccount());
    }

    private void sendEmailToAllHumanPlayers(Game game, Consumer<String> stringConsumer) {
        game.getPlayerList().stream()
                .filter(player -> PlayerType.HUMAN.equals(player.getPlayerType()))
                .filter(player -> player.getHumanUserAccount() != null)
                .map(Player::getHumanUserAccount)
                .map(UserAccount::getEmail)
                .forEach(stringConsumer);
    }
}
