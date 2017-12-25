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

        // then
        resultActions
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.host").value(testUserAccount.getUsername()))
                .andExpect(jsonPath("$.players").exists())
                .andExpect(jsonPath("$.name").value(gameName))
                .andExpect(jsonPath("$.description").value(gameDescription))
                .andExpect(jsonPath("$.maxNumberOfPlayers").value(mapSize.getMaxNumberOfPlayers()))
                .andExpect(jsonPath("$.gameState").value(GameState.WAITING_FOR_PLAYERS.toString()))
                .andExpect(jsonPath("$.host").value(testUserAccount.getUsername()))
                .andExpect(jsonPath("$.numberOfCityStates").value(mapSize.getDefaultNumberOfCityStates()))
                .andExpect(jsonPath("$.mapSize").value(mapSize.toString()));
    }

    @Test
    public void whenFindAllGamesIsInvoked_thenAllGamesAreReturned() throws Exception {
        // when
        ResultActions resultActions =
                mockMvc.perform(authenticated(prepareGet("/games/"), getTestUserAccount()));

        // then
        resultActions
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$.[0].id").value("09d50664-e171-45c6-a04c-d650caa4dc3f"));
    }

    @Test
    public void whenFindGameByIdIsInvoked_thenTheGameIsReturned() throws Exception {
        // when
        ResultActions resultActions =
                mockMvc.perform(authenticated(prepareGet("/games/09d50664-e171-45c6-a04c-d650caa4dc3f"), getTestUserAccount()));

        // then
        resultActions
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id").value("09d50664-e171-45c6-a04c-d650caa4dc3f"));
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
