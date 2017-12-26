package me.cybulski.civ5pbemserver.game;

import me.cybulski.civ5pbemserver.civilization.Civilization;
import me.cybulski.civ5pbemserver.exception.ResourceNotFoundException;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michał Cybulski
 */
public class ChangeCivilizationGameTest extends BaseGameTest {

    private TestGameFactory testGameFactory = new TestGameFactory();

    @Test
    public void whenCivilizationIsChanged_thenChangesAreVisible() {
        // given
        Game subject = testGameFactory.createNewTestGame(hostUserAccount);
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
        Game subject = testGameFactory.createNewTestGame(hostUserAccount);
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
