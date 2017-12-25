package me.cybulski.civ5pbemserver.game;

import com.google.common.collect.ImmutableSet;
import me.cybulski.civ5pbemserver.game.exception.CannotStartGameException;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Michał Cybulski
 */
public class GameStartTest extends BaseGameTest {

    @Rule
    // FIXME don't mock so much
    public MockitoRule mockitoRule = MockitoJUnit.rule().strictness(Strictness.LENIENT);

    @Mock
    private PlayerFactory playerFactory;

    @Test
    public void whenThereAreEmptyHumanPlayers_thenCannotStartGame() {
        // given
        Player firstPlayer = mock(Player.class);
        when(firstPlayer.getHumanUserAccount()).thenReturn(hostUserAccount);
        when(firstPlayer.getPlayerType()).thenReturn(PlayerType.HUMAN);
        Player secondPlayer = mock(Player.class);
        when(secondPlayer.getPlayerType()).thenReturn(PlayerType.HUMAN);

        // and
        ImmutableSet<Player> players = ImmutableSet.of(firstPlayer, secondPlayer);
        when(playerFactory.createNewPlayers(any(), any(), any())).thenReturn(players);

        // and
        Game subject = new TestGameFactory(playerFactory).createNewTestGame(hostUserAccount);

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = subject::startGame;

        // then
        assertThatThrownBy(throwingCallable).isInstanceOf(CannotStartGameException.class);
    }

    @Test
    public void whenThereAreOnlyAiAndClosedLeft_thenGameCanBeStarted() {
        // given
        Player firstPlayer = mock(Player.class);
        when(firstPlayer.getHumanUserAccount()).thenReturn(hostUserAccount);
        when(firstPlayer.getPlayerType()).thenReturn(PlayerType.HUMAN);
        Player secondPlayer = mock(Player.class);
        when(secondPlayer.getPlayerType()).thenReturn(PlayerType.AI);
        Player thirdPlayer = mock(Player.class);
        when(thirdPlayer.getPlayerType()).thenReturn(PlayerType.AI);
        Player fourthPlayer = mock(Player.class);
        when(fourthPlayer.getPlayerType()).thenReturn(PlayerType.CLOSED);

        // and
        ImmutableSet<Player> players = ImmutableSet.of(firstPlayer, secondPlayer, thirdPlayer, fourthPlayer);
        when(playerFactory.createNewPlayers(any(), any(), any())).thenReturn(players);

        // and
        Game subject = new TestGameFactory(playerFactory).createNewTestGame(hostUserAccount);

        // when
        subject.startGame();

        // then
        assertThat(subject.getGameState()).isEqualTo(GameState.WAITING_FOR_FIRST_MOVE);
    }
}
