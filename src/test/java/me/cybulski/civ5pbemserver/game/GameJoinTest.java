package me.cybulski.civ5pbemserver.game;

import me.cybulski.civ5pbemserver.game.exception.CannotJoinGameException;
import me.cybulski.civ5pbemserver.user.UserAccount;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

/**
 * @author Michał Cybulski
 */
public class GameJoinTest extends BaseGameTest {

    private TestGameFactory testGameFactory = new TestGameFactory();

    @Test
    public void whenNewGameIsCreated_thenHostIsFirstPlayer() {
        // given
        Game subject = testGameFactory.createNewTestGame(hostUserAccount);

        // expect
        assertThat(subject.getPlayers().stream()
                           .filter(player -> player.getPlayerNumber() == 1)
                           .map(Player::getHumanUserAccount)
                           .findFirst())
                .contains(hostUserAccount);
    }

    @Test
    public void whenUserIsAlreadyJoined_thenPlayerCannotJoinAgain() {
        // given
        Game subject = testGameFactory.createNewTestGame(hostUserAccount);

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> subject.joinGame(hostUserAccount);

        // then
        assertThatThrownBy(throwingCallable).isInstanceOf(CannotJoinGameException.class);
    }

    @Test
    public void whenUserAccountTriesToJoinAndThereIsPlace_thenPlayerJoins() {
        // given
        Game subject = testGameFactory.createNewTestGame(hostUserAccount);

        // and
        UserAccount anotherUserAccount = mock(UserAccount.class);

        // when
        subject.joinGame(anotherUserAccount);

        // then
        assertThat(subject.getPlayers().stream()
                           .filter(player -> player.getPlayerNumber() == 2)
                           .map(Player::getHumanUserAccount)
                           .findFirst())
                .contains(anotherUserAccount);
    }

    @Test
    public void whenUserAccountTriesToJoinAndThereIsNoPlace_thenExceptionIsThrown() {
        // given
        Game subject = testGameFactory.createNewTestGame(hostUserAccount, MapSize.DUEL);
        Player emptyPlayer = subject.getPlayers().stream()
                .filter(player -> player.getHumanUserAccount() == null)
                .findFirst()
                .get();
        emptyPlayer.changeToAi();

        // and
        UserAccount anotherUserAccount = mock(UserAccount.class);

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> subject.joinGame(anotherUserAccount);

        // then
        assertThatThrownBy(throwingCallable).isInstanceOf(CannotJoinGameException.class);
    }
}
