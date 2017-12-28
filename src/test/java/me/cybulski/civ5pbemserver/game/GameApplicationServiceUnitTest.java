package me.cybulski.civ5pbemserver.game;

import me.cybulski.civ5pbemserver.game.dto.GameOutputDTO;
import me.cybulski.civ5pbemserver.mail.MailService;
import me.cybulski.civ5pbemserver.user.UserAccount;
import me.cybulski.civ5pbemserver.user.UserAccountApplicationService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;
import org.springframework.core.convert.converter.Converter;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

/**
 * @author Michał Cybulski
 */
public class GameApplicationServiceUnitTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    @Mock
    private UserAccountApplicationService userAccountApplicationService;
    @Mock
    private GameFactory gameFactory;
    @Mock
    private GameRepository gameRepository;
    @Mock
    private GameTurnFactory gameTurnFactory;
    @Mock
    private SaveGameRepository saveGameRepository;
    @Mock
    private Converter<Game, GameOutputDTO> gameOutputDTOConverter;
    @Mock
    private CurrentGameTurnValidator currentGameTurnValidator;
    @Mock
    private SaveGameValidator saveGameValidator;
    @Mock
    private SaveGameSynchronizer saveGameSynchronizer;
    @Mock
    private MailService mailService;

    private GameApplicationService subject;

    @Mock
    private Game game;
    @Mock
    private UserAccount currentUserAccount;
    @Mock
    private MultipartFile multipartFile;

    private String gameId = UUID.randomUUID().toString();

    @Before
    public void setUp() {
        subject = new GameApplicationService(
                userAccountApplicationService,
                gameFactory,
                gameRepository,
                gameTurnFactory,
                saveGameRepository,
                gameOutputDTOConverter,
                currentGameTurnValidator,
                saveGameValidator,
                saveGameSynchronizer,
                mailService);
    }

    @Test
    public void whenFinishingNextTurnAndSaveGameValidationIsNeeded_thenSaveGameIsValidated() throws IOException {
        // given
        GameTurn nextTurn = mockNextGameTurnGeneration();

        // and
        when(game.getShouldSaveGameFilesBeValidated()).thenReturn(true);

        // when
        subject.finishTurn(gameId, multipartFile);

        // then
        verify(currentGameTurnValidator).checkCurrentTurnOrThrow(game, currentUserAccount);
        verify(game).nextTurn(nextTurn);

        // and
        verify(saveGameValidator).validateCurrentSaveFile(game);
    }

    @Test
    public void whenFinishingNextTurnAndSaveGameValidationIsNotNeeded_thenSaveGameIsNotValidated() throws IOException {
        // given
        GameTurn nextTurn = mockNextGameTurnGeneration();

        // and
        when(game.getShouldSaveGameFilesBeValidated()).thenReturn(false);

        // when
        subject.finishTurn(gameId, multipartFile);

        // then
        verify(currentGameTurnValidator).checkCurrentTurnOrThrow(game, currentUserAccount);
        verify(game).nextTurn(nextTurn);

        // and
        verifyZeroInteractions(saveGameValidator);
    }

    private GameTurn mockNextGameTurnGeneration() {
        String saveFileName = "somefilename.save";
        GameTurn currentGameTurn = mock(GameTurn.class);
        GameTurn nextTurn = mock(GameTurn.class);

        // and
        when(userAccountApplicationService.getCurrentUserAccount()).thenReturn(Optional.of(currentUserAccount));
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        when(saveGameRepository.saveFile(game, multipartFile)).thenReturn(saveFileName);

        // and
        when(game.getCurrentGameTurn()).thenReturn(Optional.of(currentGameTurn));
        List<Player> playerList = new ArrayList<>();
        when(game.getPlayerList()).thenReturn(playerList);
        when(gameTurnFactory.createNextTurn(currentGameTurn, playerList, saveFileName)).thenReturn(nextTurn);

        // and
        Player currentPlayer = mock(Player.class);
        when(nextTurn.getCurrentPlayer()).thenReturn(currentPlayer);
        UserAccount userAccount = mock(UserAccount.class);
        when(currentPlayer.getHumanUserAccount()).thenReturn(userAccount);
        when(userAccount.getEmail()).thenReturn("email@test.com");
        when(game.getName()).thenReturn("Game name!");

        return nextTurn;
    }
}
