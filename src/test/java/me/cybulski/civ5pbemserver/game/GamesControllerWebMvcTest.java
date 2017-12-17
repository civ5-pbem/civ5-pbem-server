package me.cybulski.civ5pbemserver.game;

import me.cybulski.civ5pbemserver.WebMvcIntegrationTest;
import me.cybulski.civ5pbemserver.game.dto.NewGameInputDTO;
import me.cybulski.civ5pbemserver.user.UserAccount;
import org.junit.Test;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Michał Cybulski
 */
public class GamesControllerWebMvcTest extends WebMvcIntegrationTest {

    @Test
    public void whenUserCreatesNewGame_thenGameIsReturned() throws Exception {
        // given
        String gameName = "New Game!";
        String gameDescription = "Some description!";
        MapSize mapSize = MapSize.STANDARD;
        NewGameInputDTO newGameInputDTO = NewGameInputDTO.builder()
                                                  .gameName(gameName)
                                                  .gameDescription(gameDescription)
                                                  .mapSize(mapSize)
                                                  .build();

        // and
        UserAccount testUserAccount = getTestUserAccount();

        // when
        ResultActions resultActions =
                mockMvc.perform(authenticated(preparePost("/games/new-game", newGameInputDTO), testUserAccount));

        // then
        resultActions
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.host").value(testUserAccount.getUsername()))
                .andExpect(jsonPath("$.name").value(gameName))
                .andExpect(jsonPath("$.description").value(gameDescription))
                .andExpect(jsonPath("$.mapSize").value(mapSize));
    }
}