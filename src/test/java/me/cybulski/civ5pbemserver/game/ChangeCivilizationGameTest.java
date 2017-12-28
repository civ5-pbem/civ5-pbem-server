package me.cybulski.civ5pbemserver.game;

import me.cybulski.civ5pbemserver.civilization.Civilization;
import me.cybulski.civ5pbemserver.exception.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michał Cybulski
 */
public class ChangeCivilizationGameTest extends BaseGameTest {

    private TestGameCreator testGameCreator;

    @Before
    public void setUp() {
        testGameCreator = new TestGameCreator(gameRepository);
    }

    @Test
    public void whenCivilizationIsChanged_thenChangesAreVisible() {
        // given
        Game subject = testGameCreator.createNewTestGame(hostUserAccount);
        Player hostPlayer = subject.findPlayer(hostUserAccount).orElseThrow(ResourceNotFoundException::new);

        // and
        Civilization civilization = Civilization.BABYLONIAN;

        // when
        subject.chooseCivilization(hostPlayer.getId(), civilization);

        // then
        assertThat(subject.findPlayer(hostUserAccount).map(Player::getCivilization)).contains(civilization);
    }

    @Test
    public void whenHostChangesAiCivilization_thenChangesAreVisible() {
        // given
        Game subject = testGameCreator.createNewTestGame(hostUserAccount);
        Player aiPlayer = subject.getPlayerList().get(1);
        aiPlayer.changeToAi();

        // and
        Civilization civilization = Civilization.BABYLONIAN;

        // when
        subject.chooseCivilization(aiPlayer.getId(), civilization);

        // then
        assertThat(aiPlayer.getCivilization()).isEqualTo(civilization);
    }
}
