package me.cybulski.civ5pbemserver.game;

import me.cybulski.civ5pbemserver.game.exception.CannotModifyGameException;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Michał Cybulski
 */
public class ChangePlayerGameTest extends BaseGameTest {

    private Game subject;

    @Before
    public void setUp() {
        subject = new TestGameFactory().createNewTestGame(hostUserAccount);
    }

    @Test
    public void whenFreeHumanPlayerTypeIsChangedToAi_thenChangeHappens() {
        // given
        Player player = subject.getPlayerList().get(1);
        Assert.state(PlayerType.HUMAN.equals(player.getPlayerType()), "Default players should be human");

        // when
        subject.changePlayerType(player.getId(), PlayerType.AI);

        // then
        assertThat(player.getPlayerType()).isEqualTo(PlayerType.AI);
    }

    @Test
    public void whenFreeHumanPlayerTypeIsChangedToClosed_thenChangeHappens() {
        // given
        Player player = subject.getPlayerList().get(1);
        Assert.state(PlayerType.HUMAN.equals(player.getPlayerType()), "Default players should be human");

        // when
        subject.changePlayerType(player.getId(), PlayerType.CLOSED);

        // then
        assertThat(player.getPlayerType()).isEqualTo(PlayerType.CLOSED);
    }

    @Test
    public void whenAiPlayerTypeIsChangedToHuman_thenChangeHappens() {
        // given
        Player player = subject.getPlayerList().get(1);
        Assert.state(PlayerType.HUMAN.equals(player.getPlayerType()), "Default players should be human");
        player.changeToAi();

        // when
        subject.changePlayerType(player.getId(), PlayerType.HUMAN);

        // then
        assertThat(player.getPlayerType()).isEqualTo(PlayerType.HUMAN);
    }

    @Test
    public void whenOccupiedHumanPlayerTypeIsChangedToAi_thenExceptionIsThrown() {
        // given
        Player player = subject.getPlayerList().get(0);
        Assert.state(hostUserAccount.equals(player.getHumanUserAccount()), "First player should be host");
        Assert.state(PlayerType.HUMAN.equals(player.getPlayerType()), "Default players should be human");

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> subject.changePlayerType(player.getId(), PlayerType.AI);

        // then
        assertThatThrownBy(throwingCallable).isInstanceOf(CannotModifyGameException.class);
    }

    @Test
    public void whenOccupiedHumanPlayerTypeIsChangedToClosed_thenExceptionIsThrown() {
        // given
        Player player = subject.getPlayerList().get(0);
        Assert.state(hostUserAccount.equals(player.getHumanUserAccount()), "First player should be host");
        Assert.state(PlayerType.HUMAN.equals(player.getPlayerType()), "Default players should be human");

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> subject.changePlayerType(player.getId(), PlayerType.CLOSED);

        // then
        assertThatThrownBy(throwingCallable).isInstanceOf(CannotModifyGameException.class);
    }
}
