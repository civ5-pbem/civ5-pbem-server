package me.cybulski.civ5pbemserver.game;

import me.cybulski.civ5pbemserver.game.exception.CannotStartGameException;
import me.cybulski.civ5pbemserver.user.TestUserAccountFactory;
import me.cybulski.civ5pbemserver.user.UserAccount;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Michał Cybulski
 */
public class GameStartTest extends BaseGameTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule().strictness(Strictness.LENIENT);

    private TestGameCreator testGameCreator;
    private UserAccount hostUserAccount;
    private Game subject;

    @Before
    public void setUp() {
        testGameCreator = new TestGameCreator(gameRepository);
        hostUserAccount = new TestUserAccountFactory().createNewUserAccount("host@test.com", "hostUser");
        subject = testGameCreator.createNewTestGame(hostUserAccount, MapSize.DUEL);
    }

    @Test
    public void whenThereAreEmptyHumanPlayers_thenCannotStartGame() {
        // when
        ThrowableAssert.ThrowingCallable throwingCallable = subject::startGame;

        // then
        assertThatThrownBy(throwingCallable).isInstanceOf(CannotStartGameException.class);
    }

    @Test
    public void whenThereAreOnlyAiAndClosedLeft_thenGameCanBeStarted() {
        // given
        UserAccount secondUser = new TestUserAccountFactory().createNewUserAccount("second@test.com", "secondUser");

        // and
        subject.joinGame(secondUser);

        // when
        subject.startGame();

        // then
        assertThat(subject.getGameState()).isEqualTo(GameState.WAITING_FOR_FIRST_MOVE);
    }
}
