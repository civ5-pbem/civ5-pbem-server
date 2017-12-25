package me.cybulski.civ5pbemserver.game;

import me.cybulski.civ5pbemserver.WebMvcIntegrationTest;
import me.cybulski.civ5pbemserver.game.dto.ChangePlayerTypeInputDTO;
import me.cybulski.civ5pbemserver.game.dto.ChooseCivilizationInputDTO;
import me.cybulski.civ5pbemserver.game.dto.NewGameInputDTO;
import me.cybulski.civ5pbemserver.user.TestUserAccountFactory;
import me.cybulski.civ5pbemserver.user.UserAccount;
import org.junit.Test;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Michał Cybulski
 */
public class GamesControllerWebMvcTest extends WebMvcIntegrationTest {

    private final TestUserAccountFactory testUserAccountFactory = new TestUserAccountFactory();

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
    public void whenUserJoinsGame_thenTheGameIsReturned() throws Exception {
        // given
        UserAccount userAccount = testUserAccountFactory.createNewUserAccount("testuser@test.com", "testUser");
        testEntityManager.persistAndFlush(userAccount);

        // when
        ResultActions resultActions =
                mockMvc.perform(authenticated(preparePost("/games/09d50664-e171-45c6-a04c-d650caa4dc3f/join"), userAccount));

        // then
        resultActions
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id").value("09d50664-e171-45c6-a04c-d650caa4dc3f"))
                .andExpect(jsonPath("$.players.[1].humanUserAccount").value(userAccount.getUsername()));
    }

    @Test
    public void whenHostChangesHostsCivilization_thenTheGameIsReturned() throws Exception {
        // given
        Civilization civilization = Civilization.BABYLONIAN;
        ChooseCivilizationInputDTO chooseCivilizationInputDTO = ChooseCivilizationInputDTO
                .builder()
                .civilization(civilization)
                .build();

        // when
        ResultActions resultActions =
                mockMvc.perform(authenticated(preparePost("/games/09d50664-e171-45c6-a04c-d650caa4dc3f" +
                                                                  "/players/e786803e-b6c9-4910-9122-4194734e73a7" +
                                                                  "/choose-civilization",
                                                          chooseCivilizationInputDTO),
                                              getTestUserAccount()));

        // then
        resultActions
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id").value("09d50664-e171-45c6-a04c-d650caa4dc3f"))
                .andExpect(jsonPath("$.players.[0].civilization").value(civilization.toString()));
    }

    @Test
    public void whenHostChangesAnotherHumanCivilization_thenErrorIsReturned() throws Exception {
        // given
        Civilization civilization = Civilization.BABYLONIAN;
        ChooseCivilizationInputDTO chooseCivilizationInputDTO = ChooseCivilizationInputDTO
                .builder()
                .civilization(civilization)
                .build();

        // when
        ResultActions resultActions =
                mockMvc.perform(authenticated(preparePost("/games/09d50664-e171-45c6-a04c-d650caa4dc3f" +
                                                                  "/players/71f73c36-e4af-4a1d-8f56-7874f542a905" +
                                                                  "/choose-civilization",
                                                          chooseCivilizationInputDTO),
                                              getTestUserAccount()));

        // then
        resultActions
                .andExpect(status().is(403));
    }

    @Test
    public void whenHostChangesAiCivilization_thenTheGameIsReturned() throws Exception {
        // given
        Civilization civilization = Civilization.BABYLONIAN;
        ChooseCivilizationInputDTO chooseCivilizationInputDTO = ChooseCivilizationInputDTO
                .builder()
                .civilization(civilization)
                .build();

        // when
        ResultActions resultActions =
                mockMvc.perform(authenticated(preparePost("/games/09d50664-e171-45c6-a04c-d650caa4dc3f" +
                                                                  "/players/6594c177-f39a-457e-adc7-c0300d937b4f" +
                                                                  "/choose-civilization",
                                                          chooseCivilizationInputDTO),
                                              getTestUserAccount()));

        // then
        resultActions
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id").value("09d50664-e171-45c6-a04c-d650caa4dc3f"))
                .andExpect(jsonPath("$.players.[2].civilization").value(civilization.toString()));
    }

    @Test
    public void whenHostChangesPlayerType_thenPlayerTypeIsChanged() throws Exception {
        // given
        PlayerType playerType = PlayerType.CLOSED;
        ChangePlayerTypeInputDTO changePlayerTypeInputDTO = ChangePlayerTypeInputDTO
                .builder()
                .playerType(playerType)
                .build();

        // when
        ResultActions resultActions =
                mockMvc.perform(authenticated(preparePost("/games/09d50664-e171-45c6-a04c-d650caa4dc3f" +
                                                                  "/players/71f73c36-e4af-4a1d-8f56-7874f542a905" +
                                                                  "/change-player-type",
                                                          changePlayerTypeInputDTO),
                                              getTestUserAccount()));

        // then
        resultActions
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id").value("09d50664-e171-45c6-a04c-d650caa4dc3f"))
                .andExpect(jsonPath("$.players.[1].playerType").value(playerType.toString()));
    }

    @Test
    public void whenUnauthenticated_thenExceptionIsReturned() throws Exception {
        // given
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
