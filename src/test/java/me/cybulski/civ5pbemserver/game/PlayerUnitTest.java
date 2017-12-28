package me.cybulski.civ5pbemserver.game;

import me.cybulski.civ5pbemserver.civilization.Civilization;
import me.cybulski.civ5pbemserver.user.UserAccount;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * @author Michał Cybulski
 */
public class PlayerUnitTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    private Player subject;

    private Civilization civilization = Civilization.ARABIAN;
    @Mock
    private Game game;
    @Mock
    private UserAccount humanUserAccount;

    @Before
    public void setUp() {
        int playerNumber = 2;
        subject = Player.builder()
                          .civilization(civilization)
                          .game(game)
                          .humanUserAccount(humanUserAccount)
                          .playerType(PlayerType.HUMAN)
                          .playerNumber(playerNumber)
                          .build();
    }

    @Test
    public void whenPlayerDies_thenPlayerIsDead() {
        // when
        subject.die();

        // then
        assertThat(subject.getPlayerType()).isEqualTo(PlayerType.DEAD);
    }
}