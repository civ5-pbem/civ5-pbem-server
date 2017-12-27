package me.cybulski.civ5pbemserver.game;

import me.cybulski.civ5pbemserver.game.exception.InvalidSaveGameException;
import me.cybulski.civ5pbemserver.saveparser.SaveGameDTO;
import me.cybulski.civ5pbemserver.saveparser.SaveGameParser;
import me.cybulski.civ5pbemserver.saveparser.SaveGamePlayerDTO;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;
import org.springframework.core.io.Resource;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Michał Cybulski
 */
public class SaveGameValidatorUnitTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    @Mock
    private SaveGameRepository saveGameRepository;
    @Mock
    private SaveGameParser saveGameParser;

    private SaveGameValidator subject;

    @Mock
    private SaveGameDTO saveGameDTO;
    @Mock
    private GameTurn gameTurn;

    @Before
    public void setUp() throws IOException {
        subject = new SaveGameValidator(
                saveGameRepository,
                saveGameParser);

        Resource mockResource = mock(Resource.class);
        when(saveGameRepository.loadFile(gameTurn)).thenReturn(mockResource);
        when(saveGameParser.parse(any())).thenReturn(saveGameDTO);
    }

    @Test
    public void whenEverythingIsValid_thenNoExceptionIsThrown() throws IOException {
        // given
        Player player = mock(Player.class);
        when(gameTurn.getCurrentPlayer()).thenReturn(player);
        int playerNumber = 1;
        when(player.getPlayerNumber()).thenReturn(playerNumber + 1);
        SaveGamePlayerDTO saveGamePlayerDTO = mock(SaveGamePlayerDTO.class);
        when(saveGameDTO.getPlayerWhoMoves()).thenReturn(saveGamePlayerDTO);
        when(saveGamePlayerDTO.getPlayerNumber()).thenReturn(playerNumber);

        // and
        int turnNumber = 12;
        when(gameTurn.getTurnNumber()).thenReturn(turnNumber);
        when(saveGameDTO.getTurnNumber()).thenReturn(turnNumber);

        // when
        subject.validate(gameTurn);

        // then no exception is thrown
    }

    @Test
    public void whenCurrentPlayerIsWrong_thenExceptionIsThrown() {
        // given
        Player player = mock(Player.class);
        when(gameTurn.getCurrentPlayer()).thenReturn(player);
        int playerNumber = 1;
        when(player.getPlayerNumber()).thenReturn(playerNumber);
        SaveGamePlayerDTO saveGamePlayerDTO = mock(SaveGamePlayerDTO.class);
        when(saveGameDTO.getPlayerWhoMoves()).thenReturn(saveGamePlayerDTO);
        when(saveGamePlayerDTO.getPlayerNumber()).thenReturn(playerNumber);

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> subject.validate(gameTurn);

        // then
        assertThatThrownBy(throwingCallable).isInstanceOf(InvalidSaveGameException.class);
    }

    @Test
    public void whenCurrentTurnNumberIsWrong_thenExceptionIsThrown() {
        // given
        Player player = mock(Player.class);
        when(gameTurn.getCurrentPlayer()).thenReturn(player);
        int playerNumber = 1;
        when(player.getPlayerNumber()).thenReturn(playerNumber + 1);
        SaveGamePlayerDTO saveGamePlayerDTO = mock(SaveGamePlayerDTO.class);
        when(saveGameDTO.getPlayerWhoMoves()).thenReturn(saveGamePlayerDTO);
        when(saveGamePlayerDTO.getPlayerNumber()).thenReturn(playerNumber);

        // and
        int turnNumber = 12;
        when(gameTurn.getTurnNumber()).thenReturn(turnNumber + 1);
        when(saveGameDTO.getTurnNumber()).thenReturn(turnNumber);

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> subject.validate(gameTurn);

        // then
        assertThatThrownBy(throwingCallable).isInstanceOf(InvalidSaveGameException.class);
    }
}
