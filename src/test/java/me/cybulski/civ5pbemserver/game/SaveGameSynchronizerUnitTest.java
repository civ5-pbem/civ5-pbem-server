package me.cybulski.civ5pbemserver.game;

import com.google.common.collect.ImmutableList;
import me.cybulski.civ5pbemserver.saveparser.SaveGameDTO;
import me.cybulski.civ5pbemserver.saveparser.SaveGameDTOTestCreator;
import me.cybulski.civ5pbemserver.saveparser.SaveGameParser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static org.mockito.Mockito.*;

/**
 * @author Michał Cybulski
 */
public class SaveGameSynchronizerUnitTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    @Mock
    private SaveGameRepository saveGameRepository;
    @Mock
    private SaveGameParser saveGameParser;

    private SaveGameSynchronizer subject;

    @Mock
    private Game game;
    @Mock
    private Resource saveGameResource;
    @Mock
    private File saveGameFile;
    private String saveGameFilename = "someSave";

    @Before
    public void setUp() throws IOException {
        subject = new SaveGameSynchronizer(
                saveGameRepository,
                saveGameParser);

        when(saveGameRepository.loadFile(game, saveGameFilename)).thenReturn(saveGameResource);
        when(saveGameResource.getFile()).thenReturn(saveGameFile);
    }

    @Test
    public void whenDeadPlayersAreSynchronized_thenPlayersDie() throws IOException {
        // given
        SaveGameDTO saveGameDTO = new SaveGameDTOTestCreator().prepareValidSaveGameDTOWith4Players();
        when(saveGameParser.parse(saveGameFile)).thenReturn(saveGameDTO);

        // and
        Player player1 = mock(Player.class);
        Player player2 = mock(Player.class);
        Player player3 = mock(Player.class);
        Player player4 = mock(Player.class);
        when(game.getPlayerList()).thenReturn(ImmutableList.of(player1, player2, player3, player4));

        // when
        subject.synchronizeDeadPlayers(game, saveGameFilename);

        // then
        verifyZeroInteractions(player1);
        verify(player2).die();
        verifyZeroInteractions(player3);
    }

    @Test
    public void whenTursAreSynchronized_thenGameTurnSyncIsInvoked() throws IOException {
        // given
        GameTurn currentGameTurn = mock(GameTurn.class);
        when(game.getCurrentGameTurn()).thenReturn(Optional.of(currentGameTurn));

        // and
        SaveGameDTO saveGameDTO = new SaveGameDTOTestCreator().prepareValidSaveGameDTOWith4Players();
        when(saveGameParser.parse(saveGameFile)).thenReturn(saveGameDTO);

        // and
        Player player1 = mock(Player.class);
        Player player2 = mock(Player.class);
        Player player3 = mock(Player.class);
        Player player4 = mock(Player.class);
        when(game.getPlayerList()).thenReturn(ImmutableList.of(player1, player2, player3, player4));

        // when
        subject.synchronizeTurnNumberAndCurrentPlayer(game, saveGameFilename);

        // verify
        currentGameTurn.syncrhonizeWithSaveGame(saveGameDTO.getTurnNumber(), player1);
    }
}