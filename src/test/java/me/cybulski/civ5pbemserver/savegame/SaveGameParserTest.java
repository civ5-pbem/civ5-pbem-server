package me.cybulski.civ5pbemserver.savegame;

import me.cybulski.civ5pbemserver.savegame.dto.SaveGameDTO;
import me.cybulski.civ5pbemserver.savegame.dto.SaveGamePlayerStatus;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michał Cybulski
 */
public class SaveGameParserTest {

    private static final String SAVE_GAMES_ROOT = "save-games/";

    private SaveGameParser subject;

    @Before
    public void setUp() {
        subject = new SaveGameParser(new SaveGameHelper());
    }

    @Test
    public void before_any_turn() throws IOException {
        // given
        String fileName = "before_any_turn.Civ5Save";

        // when
        SaveGameDTO saveGameDTO = subject.parse(getSaveFileInputStream(fileName));

        // then
        assertThat(saveGameDTO.getGameVersion()).isEqualTo("1.0.3.279(130961)");
        assertThat(saveGameDTO.getTurnNumber()).isEqualTo(0);
        assertThat(saveGameDTO.getPlayers().size()).isEqualTo(4);
        assertThat(saveGameDTO.getPlayerWhoMoves().getPlayerNumber()).isEqualTo(0);
        assertPlayer(saveGameDTO, 0, "", SaveGamePlayerStatus.HUMAN);
        assertPlayer(saveGameDTO, 1, "", SaveGamePlayerStatus.HUMAN);
        assertPlayer(saveGameDTO, 2, "", SaveGamePlayerStatus.HUMAN);
        assertPlayer(saveGameDTO, 3, "", SaveGamePlayerStatus.AI);
    }

    @Test
    public void after_1st_player_1st_turn() throws IOException {
        // given
        String fileName = "after_1st_player_1st_turn.Civ5Sample";

        // when
        SaveGameDTO saveGameDTO = subject.parse(getSaveFileInputStream(fileName));

        // then
        assertThat(saveGameDTO.getGameVersion()).isEqualTo("1.0.3.279(130961)");
        assertThat(saveGameDTO.getTurnNumber()).isEqualTo(1);
        assertThat(saveGameDTO.getPlayers().size()).isEqualTo(4);
        assertThat(saveGameDTO.getPlayerWhoMoves().getPlayerNumber()).isEqualTo(1);
        assertPlayer(saveGameDTO, 0, "", SaveGamePlayerStatus.HUMAN);
        assertPlayer(saveGameDTO, 1, "", SaveGamePlayerStatus.HUMAN);
        assertPlayer(saveGameDTO, 2, "", SaveGamePlayerStatus.HUMAN);
        assertPlayer(saveGameDTO, 3, "", SaveGamePlayerStatus.AI);
    }

    @Test
    public void third_player_adds_password_1st_turn() throws IOException {
        // given
        String fileName = "third_player_adds_password_1st_turn.Civ5Save";

        // when
        SaveGameDTO saveGameDTO = subject.parse(getSaveFileInputStream(fileName));

        // then
        assertThat(saveGameDTO.getGameVersion()).isEqualTo("1.0.3.279(130961)");
        assertThat(saveGameDTO.getTurnNumber()).isEqualTo(1);
        assertThat(saveGameDTO.getPlayers().size()).isEqualTo(4);
        assertThat(saveGameDTO.getPlayerWhoMoves().getPlayerNumber()).isEqualTo(2);
        assertPlayer(saveGameDTO, 0, "", SaveGamePlayerStatus.HUMAN);
        assertPlayer(saveGameDTO, 1, "", SaveGamePlayerStatus.HUMAN);
        assertPlayer(saveGameDTO, 2, "koty", SaveGamePlayerStatus.HUMAN);
        assertPlayer(saveGameDTO, 3, "", SaveGamePlayerStatus.AI);
    }

    @Test
    public void first_player_adds_password_2nd_turn() throws IOException {
        // given
        String fileName = "first_player_adds_password_2nd_turn.Civ5Save";

        // when
        SaveGameDTO saveGameDTO = subject.parse(getSaveFileInputStream(fileName));

        // then
        assertThat(saveGameDTO.getGameVersion()).isEqualTo("1.0.3.279(130961)");
        assertThat(saveGameDTO.getTurnNumber()).isEqualTo(2);
        assertThat(saveGameDTO.getPlayers().size()).isEqualTo(4);
        assertThat(saveGameDTO.getPlayerWhoMoves().getPlayerNumber()).isEqualTo(0);
        assertPlayer(saveGameDTO, 0, "mistrze", SaveGamePlayerStatus.HUMAN);
        assertPlayer(saveGameDTO, 1, "", SaveGamePlayerStatus.HUMAN);
        assertPlayer(saveGameDTO, 2, "koty", SaveGamePlayerStatus.HUMAN);
        assertPlayer(saveGameDTO, 3, "", SaveGamePlayerStatus.AI);
    }

    @Test
    public void second_player_adds_password_2nd_turn() throws IOException {
        // given
        String fileName = "second_player_adds_password_2nd_turn.Civ5Save";

        // when
        SaveGameDTO saveGameDTO = subject.parse(getSaveFileInputStream(fileName));

        // then
        assertThat(saveGameDTO.getGameVersion()).isEqualTo("1.0.3.279(130961)");
        assertThat(saveGameDTO.getTurnNumber()).isEqualTo(2);
        assertThat(saveGameDTO.getPlayers().size()).isEqualTo(4);
        assertThat(saveGameDTO.getPlayerWhoMoves().getPlayerNumber()).isEqualTo(1);
        assertPlayer(saveGameDTO, 0, "mistrze", SaveGamePlayerStatus.HUMAN);
        assertPlayer(saveGameDTO, 1, "hamsters", SaveGamePlayerStatus.HUMAN);
        assertPlayer(saveGameDTO, 2, "koty", SaveGamePlayerStatus.HUMAN);
        assertPlayer(saveGameDTO, 3, "", SaveGamePlayerStatus.AI);
    }

    @Test
    public void after_26_turn() throws IOException {
        // given
        String fileName = "after_26_turn.Civ5Save";

        // when
        SaveGameDTO saveGameDTO = subject.parse(getSaveFileInputStream(fileName));

        // then
        assertThat(saveGameDTO.getGameVersion()).isEqualTo("1.0.3.279(130961)");
        assertThat(saveGameDTO.getTurnNumber()).isEqualTo(27);
        assertThat(saveGameDTO.getPlayers().size()).isEqualTo(4);
        assertThat(saveGameDTO.getPlayerWhoMoves().getPlayerNumber()).isEqualTo(0);
        assertPlayer(saveGameDTO, 0, "mistrze", SaveGamePlayerStatus.HUMAN);
        assertPlayer(saveGameDTO, 1, "hamsters", SaveGamePlayerStatus.HUMAN);
        assertPlayer(saveGameDTO, 2, "koty", SaveGamePlayerStatus.HUMAN);
        assertPlayer(saveGameDTO, 3, "", SaveGamePlayerStatus.AI);
    }

    @Test
    public void during_3rd_player_41st_turn_after_kill() throws IOException {
        // given
        String fileName = "during_3rd_player_41st_turn_after_kill.Civ5Save";

        // when
        SaveGameDTO saveGameDTO = subject.parse(getSaveFileInputStream(fileName));

        // then
        assertThat(saveGameDTO.getGameVersion()).isEqualTo("1.0.3.279(130961)");
        assertThat(saveGameDTO.getTurnNumber()).isEqualTo(41);
        assertThat(saveGameDTO.getPlayers().size()).isEqualTo(4);
        assertThat(saveGameDTO.getPlayerWhoMoves().getPlayerNumber()).isEqualTo(2);
        assertPlayer(saveGameDTO, 0, "mistrze", SaveGamePlayerStatus.HUMAN);
        assertPlayer(saveGameDTO, 1, "kot", SaveGamePlayerStatus.DEAD);
        assertPlayer(saveGameDTO, 2, "koty", SaveGamePlayerStatus.HUMAN);
        assertPlayer(saveGameDTO, 3, "", SaveGamePlayerStatus.AI);
    }

    @Test
    public void playerNamesAreRead() throws IOException {
        // given
        String fileName = "after_1st_player_1st_turn.Civ5Sample";

        // when
        SaveGameDTO saveGameDTO = subject.parse(getSaveFileInputStream(fileName));

        // then
        assertThat(saveGameDTO.getPlayers().get(0).getPlayerName()).isEqualTo("Player 1");
        assertThat(saveGameDTO.getPlayers().get(1).getPlayerName()).isEqualTo("Player 2");
        assertThat(saveGameDTO.getPlayers().get(2).getPlayerName()).isEqualTo("Player 3");
    }

    private void assertPlayer(SaveGameDTO saveGameDTO, int index, String password, SaveGamePlayerStatus playerStatus) {
        assertThat(saveGameDTO.getPlayers().get(index).getPassword()).isEqualTo(password);
        assertThat(saveGameDTO.getPlayers().get(index).getPlayerNumber()).isEqualTo(index);
        assertThat(saveGameDTO.getPlayers().get(index).getPlayerStatus()).isEqualTo(playerStatus);
    }

    private File getSaveFileInputStream(String fileName) {
        return new File(this.getClass().getClassLoader().getResource(SAVE_GAMES_ROOT + fileName).getFile());
    }
}
