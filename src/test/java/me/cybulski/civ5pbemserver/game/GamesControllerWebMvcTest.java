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

    private final String gameName = "New Game!";
    private final String gameDescription = "Some description!";
    private final MapSize mapSize = MapSize.STANDARD;

    @Test
    public void whenUserCreatesNewGame_thenGameIsReturned() throws Exception {
        // given
        NewGameInputDTO newGameInputDTO = prepareNewGameInputDTO();

        // and
        UserAccount testUserAccount = getTestUserAccount();

        // when
        ResultActions resultActions =
                mockMvc.perform(authenticated(preparePost("/games/new-game", newGameInputDTO), testUserAccount));

        // and
        testEntityManager.flush();

        // then
        resultActions
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.host").value(testUserAccount.getUsername()))
                .andExpect(jsonPath("$.name").value(gameName))
                .andExpect(jsonPath("$.description").value(gameDescription))
                .andExpect(jsonPath("$.maxNumberOfPlayers").value(mapSize.getMaxNumberOfPlayers()))
                .andExpect(jsonPath("$.gameState").value(GameState.WAITING_FOR_PLAYERS.toString()))
                .andExpect(jsonPath("$.host").value(testUserAccount.getUsername()))
                .andExpect(jsonPath("$.numberOfCityStates").value(mapSize.getDefaultNumberOfCityStates()))
                .andExpect(jsonPath("$.mapSize").value(mapSize.toString()));
    }

    @Test
    public void whenUnauthenticated_thenExceptionIsReturned() throws Exception {
        NewGameInputDTO newGameInputDTO = prepareNewGameInputDTO();

        // when
        ResultActions resultActions = mockMvc.perform(preparePost("/games/new-game", newGameInputDTO));

        // then
        resultActions.andExpect(status().is(403));
    }

    private NewGameInputDTO prepareNewGameInputDTO() {
        return NewGameInputDTO.builder()
                .gameName(gameName)
                .gameDescription(gameDescription)
                .mapSize(mapSize)
                .build();
    }
}
