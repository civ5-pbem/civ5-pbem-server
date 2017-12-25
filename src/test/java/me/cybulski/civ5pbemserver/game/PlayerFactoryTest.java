package me.cybulski.civ5pbemserver.game;

import me.cybulski.civ5pbemserver.user.UserAccount;
import org.junit.Test;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author Michał Cybulski
 */
public class PlayerFactoryTest {

    private PlayerFactory subject = new PlayerFactory();

    @Test
    public void whenPlayerSetIsCreated_thenHostIsFirstPlayer() {
        // given
        UserAccount host = mock(UserAccount.class);
        Game game = mock(Game.class);

        // when
        Set<Player> newPlayers = subject.createNewPlayers(host, game, 4);

        // then
        Optional<Player> firstPlayer = newPlayers.stream().filter(player -> player.getPlayerNumber() == 1).findFirst();
        assertThat(firstPlayer).isPresent();
        assertThat(firstPlayer.map(Player::getPlayerType)).contains(PlayerType.HUMAN);
        assertThat(firstPlayer.map(Player::getHumanUserAccount)).contains(host);
    }
}
