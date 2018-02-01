package me.cybulski.civ5pbemserver.game;

import com.google.common.collect.ImmutableList;
import me.cybulski.civ5pbemserver.savegame.SaveGameParser;
import me.cybulski.civ5pbemserver.savegame.SaveGameWriter;
import me.cybulski.civ5pbemserver.savegame.dto.SaveGameDTO;
import me.cybulski.civ5pbemserver.savegame.dto.SaveGamePlayerDTO;
import me.cybulski.civ5pbemserver.user.UserAccount;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Michał Cybulski
 */
public class DynamicSaveGameGeneratorUnitTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    @Mock
    private SaveGameParser saveGameParser;
    @Mock
    private SaveGameWriter saveGameWriter;

    private DynamicSaveGameGenerator subject;

    @Before
    public void setUp() {
        subject = new DynamicSaveGameGenerator(saveGameParser, saveGameWriter);
    }

    @Test
    public void whenGeneratingSaveGame_thenPlayerNamesAreReplaced() throws IOException {
        // given
        GameTurn gameTurn = mock(GameTurn.class);
        Game game = mock(Game.class);
        when(gameTurn.getGame()).thenReturn(game);

        // and
        Player player1 = mock(Player.class);
        when(player1.getPlayerType()).thenReturn(PlayerType.HUMAN);
        UserAccount player1UserAccount = mock(UserAccount.class);
        String player1Name = "test-user";
        when(player1UserAccount.getUsername()).thenReturn(player1Name);
        when(player1.getHumanUserAccount()).thenReturn(player1UserAccount);

        // and
        Player player2 = mock(Player.class);
        when(player2.getPlayerType()).thenReturn(PlayerType.AI);

        // and
        when(game.getPlayerList()).thenReturn(ImmutableList.of(player1, player2));

        // and
        File originalFile = mock(File.class);
        SaveGameDTO saveGameDTO = mock(SaveGameDTO.class);
        List<SaveGamePlayerDTO> playerDTOList = ImmutableList.of(
                SaveGamePlayerDTO.builder().playerNumber(0).build(),
                SaveGamePlayerDTO.builder().playerNumber(1).build()
        );

        // and
        when(saveGameParser.parse(originalFile)).thenReturn(saveGameDTO);
        when(saveGameDTO.getPlayers()).thenReturn(playerDTOList);

        // and
        List<SaveGameWriter.ByteBlock> byteBlocks = ImmutableList.of();
        when(saveGameWriter.preparePlayerNameByteBlocks(playerDTOList)).thenReturn(byteBlocks);

        // when
        subject.generateSaveGameForNextPlayer(gameTurn, originalFile);

        // then
        assertThat(playerDTOList.get(0).getPlayerName()).isEqualTo(player1Name);
        assertThat(playerDTOList.get(1).getPlayerName()).isEqualTo("");

        // and
        verify(saveGameWriter).replaceBlocks(eq(originalFile), any(), eq(byteBlocks));
    }
}