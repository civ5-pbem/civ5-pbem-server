package me.cybulski.civ5pbemserver.game;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author Michał Cybulski
 */
public class GameTurnUnitTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    private GameTurn subject;

    @Mock
    private Game game;
    @Mock
    private Player currentPlayer;
    @Mock
    private GameTurn previousGameTurn;
    private String saveFilename = UUID.randomUUID().toString();

    @Before
    public void setUp() {
        subject = GameTurn.builder()
                          .currentPlayer(currentPlayer)
                          .game(game)
                          .previousGameTurn(previousGameTurn)
                          .turnNumber(15)
                          .saveFilename(saveFilename)
                          .build();
    }

    @Test
    public void whenGameTurnIsBeingSynchronized_thenGameTurnIsSynchronized() {
        // given
        int newTurnNumber = 25;
        Player newCurrentPlayer = mock(Player.class);

        // when
        subject.syncrhonizeWithSaveGame(newTurnNumber, newCurrentPlayer);

        // then
        assertThat(subject.getTurnNumber()).isEqualTo(newTurnNumber);
        assertThat(subject.getCurrentPlayer()).isEqualTo(newCurrentPlayer);
    }
}