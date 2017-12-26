package me.cybulski.civ5pbemserver.game;

import me.cybulski.civ5pbemserver.WebMvcIntegrationTest;
import me.cybulski.civ5pbemserver.game.dto.ChangePlayerTypeInputDTO;
import me.cybulski.civ5pbemserver.game.dto.ChooseCivilizationInputDTO;
import me.cybulski.civ5pbemserver.game.dto.NewGameInputDTO;
import me.cybulski.civ5pbemserver.user.TestUserAccountFactory;
import me.cybulski.civ5pbemserver.user.UserAccount;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;

import static me.cybulski.civ5pbemserver.game.GameState.WAITING_FOR_FIRST_MOVE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Michał Cybulski
 */
public class GamesControllerWebMvcTest extends WebMvcIntegrationTest {

    @Autowired
    private GameRepository gameRepository;

    private final TestUserAccountFactory testUserAccountFactory = new TestUserAccountFactory();
    private Game game;
    private UserAccount hostUserAccount;
    private UserAccount secondUserAccount;
    private MapSize mapSize;

    @Before
    public void setUp() {
        // setting up the users
        hostUserAccount = testUserAccountFactory.createNewUserAccount("host@test.com", "hostUser");
        secondUserAccount = testUserAccountFactory.createNewUserAccount("second@test.com", "secondUser");
        testEntityManager.persistAndFlush(hostUserAccount);
        testEntityManager.persistAndFlush(secondUserAccount);

        // setting up the game
        mapSize = MapSize.DUEL;
        game = new TestGameFactory(gameRepository).createNewTestGame(hostUserAccount, mapSize);
        testEntityManager.persistAndFlush(game);
    }

    @Test
    public void whenUserCreatesNewGame_thenGameIsReturned() throws Exception {
        // given
        NewGameInputDTO newGameInputDTO = prepareNewGameInputDTO();

        // when
        ResultActions resultActions =
                mockMvc.perform(authenticated(preparePost("/games/new-game", newGameInputDTO), hostUserAccount));

        // then
        resultActions
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.host").value(hostUserAccount.getUsername()))
                .andExpect(jsonPath("$.players").exists())
                .andExpect(jsonPath("$.name").value(game.getName()))
                .andExpect(jsonPath("$.description").value(game.getDescription()))
                .andExpect(jsonPath("$.gameState").value(GameState.WAITING_FOR_PLAYERS.toString()))
                .andExpect(jsonPath("$.host").value(hostUserAccount.getUsername()))
                .andExpect(jsonPath("$.numberOfCityStates").value(mapSize.getDefaultNumberOfCityStates()))
                .andExpect(jsonPath("$.mapSize").value(mapSize.toString()));
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
                .gameName(game.getName())
                .gameDescription(game.getDescription())
                .mapSize(mapSize)
                .build();
    }

    @Test
    public void whenFindAllGamesIsInvoked_thenAllGamesAreReturned() throws Exception {
        // when
        ResultActions resultActions =
                mockMvc.perform(authenticated(prepareGet("/games"), hostUserAccount));

        // then
        resultActions
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$.[0].id").value(game.getId()));
    }

    @Test
    public void whenFindGameByIdIsInvoked_thenTheGameIsReturned() throws Exception {
        // when
        ResultActions resultActions =
                mockMvc.perform(authenticated(prepareGet("/games/" + game.getId()), hostUserAccount));

        // then
        resultActions
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id").value(game.getId()));
    }

    @Test
    public void whenUserJoinsGame_thenTheGameIsReturned() throws Exception {
        // when
        ResultActions resultActions =
                mockMvc.perform(authenticated(preparePost("/games/" + game.getId() + "/join"),
                                              secondUserAccount));

        // then
        resultActions
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id").value(game.getId()))
                .andExpect(jsonPath("$.players.[1].humanUserAccount").value(secondUserAccount.getUsername()));
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
                mockMvc.perform(authenticated(preparePost("/games/" + game.getId() +
                                                                  "/players/" + game.getPlayerList().get(0).getId() +
                                                                  "/choose-civilization",
                                                          chooseCivilizationInputDTO),
                                              hostUserAccount));

        // then
        resultActions
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id").value(game.getId()))
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
                mockMvc.perform(authenticated(preparePost("/games/" + game.getId() +
                                                                  "/players/" + game.getPlayerList().get(1).getId() +
                                                                  "/choose-civilization",
                                                          chooseCivilizationInputDTO),
                                              hostUserAccount));

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

        // and
        game.getPlayerList().get(1).changeToAi();

        // when
        ResultActions resultActions =
                mockMvc.perform(authenticated(preparePost("/games/" + game.getId() +
                                                                  "/players/" + game.getPlayerList().get(1).getId() +
                                                                  "/choose-civilization",
                                                          chooseCivilizationInputDTO),
                                              hostUserAccount));

        // then
        resultActions
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id").value(game.getId()))
                .andExpect(jsonPath("$.players.[1].civilization").value(civilization.toString()));
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
                mockMvc.perform(authenticated(preparePost("/games/" + game.getId() +
                                                                  "/players/" + game.getPlayerList().get(1).getId() +
                                                                  "/change-player-type",
                                                          changePlayerTypeInputDTO),
                                              hostUserAccount));

        // then
        resultActions
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id").value(game.getId()))
                .andExpect(jsonPath("$.players.[1].playerType").value(playerType.toString()));
    }

    @Test
    public void whenHostKicksPlayerOut_thenTheGameIsReturned() throws Exception {
        // given
        mockMvc.perform(authenticated(preparePost("/games/" + game.getId() + "/join"), hostUserAccount));

        // when
        ResultActions resultActions =
                mockMvc.perform(authenticated(preparePost("/games/" + game.getId() +
                                                                  "/players/" + game.getPlayerList().get(1).getId() + "/kick"),
                                              hostUserAccount));

        // then
        resultActions
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id").value(game.getId()))
                .andExpect(jsonPath("$.players.[1].humanUserAccount").isEmpty());
    }

    @Test
    public void whenRandomUserKicksPlayerOut_thenErrorIsReturned() throws Exception {
        // when
        ResultActions resultActions =
                mockMvc.perform(authenticated(preparePost("/games/" + game.getId() +
                                                                  "/players/" + game.getPlayerList().get(0).getId() + "/kick"),
                                              secondUserAccount));

        // then
        resultActions
                .andExpect(status().is(403));
    }

    @Test
    public void whenUserLeaves_thenTheGameIsReturned() throws Exception {
        // given
        mockMvc.perform(authenticated(preparePost("/games/" + game.getId() + "/join"), secondUserAccount));

        // when
        ResultActions resultActions =
                mockMvc.perform(authenticated(preparePost("/games/" + game.getId() + "/leave"),
                                              secondUserAccount));

        // then
        resultActions
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id").value(game.getId()))
                .andExpect(jsonPath("$.players.[1].humanUserAccount").isEmpty());
    }

    @Test
    public void whenAllPlayersAreReady_thenTheGameCanBeStartedByHost() throws Exception {
        // given
        mockMvc.perform(authenticated(preparePost("/games/" + game.getId() + "/join"), secondUserAccount));

        // when
        ResultActions resultActions =
                mockMvc.perform(authenticated(preparePost("/games/" + game.getId() + "/start"),
                                              hostUserAccount));

        // then
        resultActions
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.gameState").value(WAITING_FOR_FIRST_MOVE.toString()));
    }

    @Test
    public void whenAllPlayersAreReady_thenTheGameCannotBeStartedByAnotherPlayer() throws Exception {
        // given
        mockMvc.perform(authenticated(preparePost("/games/" + game.getId() + "/join"), secondUserAccount));

        // when
        ResultActions resultActions =
                mockMvc.perform(authenticated(preparePost("/games/" + game.getId() + "/start"),
                                              secondUserAccount));

        // then
        resultActions
                .andExpect(status().is(403));
    }

    @Test
    public void whenGameIsStarted_thenHostCanUploadSave() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile("file",
                                                       "test.txt",
                                                       "text/plain",
                                                       "Spring Framework".getBytes());

        // and
        mockMvc.perform(authenticated(preparePost("/games/" + game.getId() + "/join"), secondUserAccount));
        mockMvc.perform(authenticated(preparePost("/games/" + game.getId() + "/start"), hostUserAccount));

        // when
        ResultActions resultActions = mockMvc.perform(authenticated(multipart("/games/" + game.getId()
                                                                                      + "/finish-turn")
                                                                            .file(file),
                                                                    hostUserAccount));

        // then
        resultActions
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id").value(game.getId()));
    }

    @Test
    public void whenFirstMoveIsDone_thenSecondPlayerCanDownloadSave() throws Exception {
        // given
        byte[] bytes = "Spring Framework".getBytes();
        MockMultipartFile file = new MockMultipartFile("file",
                                                       "test.txt",
                                                       "text/plain",
                                                       bytes);

        // and
        mockMvc.perform(authenticated(preparePost("/games/" + game.getId() + "/join"), secondUserAccount));
        mockMvc.perform(authenticated(preparePost("/games/" + game.getId() + "/start"), hostUserAccount));
        mockMvc.perform(authenticated(multipart("/games/" + game.getId() + "/finish-turn").file(file),
                                      hostUserAccount));

        // when
        ResultActions resultActions = mockMvc.perform(authenticated(prepareGet("/games/" + game.getId() + "/save-game"),
                                                                    secondUserAccount));
        // then
        resultActions
                .andExpect(status().is(200))
                .andExpect(content().bytes(bytes));
    }
}
