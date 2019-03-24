package me.cybulski.civ5pbemserver.game;

import me.cybulski.civ5pbemserver.user.TestUserAccountFactory;
import me.cybulski.civ5pbemserver.user.UserAccount;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;
import org.mockito.stubbing.Answer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @author Michał Cybulski
 */
public class GameTurnFactoryTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    @Mock
    private GameRepository gameRepository;
    @Mock
    private GameTurnRepository gameTurnRepository;
    private GameTurnFactory subject;

    @Before
    public void setUp() {
        subject = new GameTurnFactory(gameTurnRepository);
        when(gameRepository.save(any())).thenAnswer((Answer<Game>) invocation -> invocation.getArgument(0));
        when(gameTurnRepository.save(any())).thenAnswer((Answer<GameTurn>) invocation -> invocation.getArgument(0));
    }

    @Test
    public void testCreatingTurns() {
        // given
        TestUserAccountFactory testUserAccountFactory = new TestUserAccountFactory();
        UserAccount host = testUserAccountFactory.instance()
                .withUsername("hostUser")
                .withEmail("host@test.com")
                .withConfirmedRegistration()
                .toBuildStep()
                .build();
        UserAccount secondUser = testUserAccountFactory.instance()
                .withUsername("secondUser")
                .withEmail("second@test.com")
                .withConfirmedRegistration()
                .toBuildStep()
                .build();

        // and
        Game game = new TestGameCreator(gameRepository).createNewTestGame(host, MapSize.TINY);
        game.getPlayerList().get(2).changeToAi();
        game.getPlayerList().get(3).changeToAi();
        game.joinGame(secondUser);
        game.startGame();

        // and
        Player firstPlayer = game.findPlayer(host).orElseThrow(RuntimeException::new);
        Player secondPlayer = game.findPlayer(secondUser).orElseThrow(RuntimeException::new);

        // when
        GameTurn firstGameTurn = subject.createFirstGameTurn(game);

        // then
        assertThat(firstGameTurn.getCurrentPlayer()).isEqualTo(firstPlayer);
        assertThat(firstGameTurn.getSaveFilename()).isNull();
        assertThat(firstGameTurn.getTurnNumber()).isEqualTo(0);
        assertThat(firstGameTurn.getPreviousGameTurn()).isNull();

        // and when
        String firstSaveFilename = "first-filename.civ5Save";
        GameTurn secondTurn = subject.createNextTurn(firstGameTurn, game.getPlayerList(), firstSaveFilename);

        // then
        assertThat(secondTurn.getCurrentPlayer()).isEqualTo(secondPlayer);
        assertThat(secondTurn.getSaveFilename()).isEqualTo(firstSaveFilename);
        assertThat(secondTurn.getTurnNumber()).isEqualTo(0);
        assertThat(secondTurn.getPreviousGameTurn()).isEqualTo(firstGameTurn);

        // and when
        String secondSaveFilename = "second-filename.civ5Save";
        GameTurn thirdTurn = subject.createNextTurn(secondTurn, game.getPlayerList(), secondSaveFilename);

        // then
        assertThat(thirdTurn.getCurrentPlayer()).isEqualTo(firstPlayer);
        assertThat(thirdTurn.getSaveFilename()).isEqualTo(secondSaveFilename);
        assertThat(thirdTurn.getTurnNumber()).isEqualTo(1);
        assertThat(thirdTurn.getPreviousGameTurn()).isEqualTo(secondTurn);
    }
}
