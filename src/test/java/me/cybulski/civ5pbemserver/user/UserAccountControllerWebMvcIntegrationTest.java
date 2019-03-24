package me.cybulski.civ5pbemserver.user;

import me.cybulski.civ5pbemserver.WebMvcIntegrationTest;
import me.cybulski.civ5pbemserver.user.dto.RegisterInputDTO;
import me.cybulski.civ5pbemserver.user.dto.ResetAccessTokenDTO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * @author Michał Cybulski
 */
public class UserAccountControllerWebMvcIntegrationTest extends WebMvcIntegrationTest {

    @Autowired
    private UserAccountApplicationService userAccountApplicationService;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Test
    public void whenUserRegisters_thenNewUserAccountIsCreated() throws Exception {
        // given
        String email = "michal@cybulski.me";
        String username = "mcybulsk";
        RegisterInputDTO data = RegisterInputDTO.builder()
                .email(email)
                .username(username)
                .build();

        // when
        ResultActions resultAction = mockMvc.perform(preparePost("/user-accounts/register", data));

        // then
        resultAction
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.username").value(username));
    }

    @Test
    public void whenUnauthenticatedUserVisitsCurrent_thenAnonymousUserIsReturned() throws Exception {
        // when
        ResultActions resultActions = mockMvc.perform(prepareGet("/user-accounts/current"));

        // then
        resultActions
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.email").value("anonymousUser"))
                .andExpect(jsonPath("$.roles.length()").value(1))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_ANONYMOUS"));
    }

    @Test
    public void whenRegisteredUserVisitsCurrent_thenUserIsReturned() throws Exception {
        // given
        String email = "michal@cybulski.me";
        String username = "mcybulsk";

        // and
        userAccountApplicationService.registerUserAccount(email, username);
        UserAccount userAccount = userAccountApplicationService.findUserByEmail(email).get();

        // when
        ResultActions resultActions =
                mockMvc.perform(authenticated(prepareGet("/user-accounts/current"), userAccount));

        // then
        resultActions
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.roles.length()").value(1))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"));
    }

    @Test
    public void whenTestUserVisitsCurrent_thenTestUserIsReturned() throws Exception {
        // given
        UserAccount userAccount = new TestUserAccountFactory().instance()
                .withUsername("hostUser")
                .withEmail("host@test.com")
                .withConfirmedRegistration()
                .toBuildStep()
                .build(testEntityManager);

        // when
        ResultActions resultActions =
                mockMvc.perform(authenticated(prepareGet("/user-accounts/current"), userAccount));

        // then
        resultActions
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.email").value(userAccount.getEmail()))
                .andExpect(jsonPath("$.username").value(userAccount.getUsername()))
                .andExpect(jsonPath("$.roles.length()").value(1))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"));
    }

    @Test
    public void whenUserStartsNonExistingAccountAccessTokenResetProcessThen200IsSentBack() throws Exception {
        // given
        ResetAccessTokenDTO resetAccessTokenDTO = ResetAccessTokenDTO.builder().email("someRandomEmail@test.com").build();

        // when
        ResultActions resultActions =
                mockMvc.perform(preparePost("/user-accounts/reset-access-token", resetAccessTokenDTO));

        // then
        resultActions.andExpect(status().is(200));
    }

    @Test
    public void accessTokenResetProcessWorks() throws Exception {
        // given
        UserAccount userAccount = new TestUserAccountFactory().instance()
                .withDefaultUsername()
                .withDefaultEmail()
                .withConfirmedRegistration()
                .toBuildStep()
                .build(testEntityManager);

        // and
        ResetAccessTokenDTO resetAccessTokenDTO = ResetAccessTokenDTO.builder().email(userAccount.getEmail()).build();

        // when
        ResultActions startProcessActions =
                mockMvc.perform(preparePost("/user-accounts/reset-access-token", resetAccessTokenDTO));

        // then
        startProcessActions.andExpect(status().is(200));

        // and given
        UserAccount userAccountWithNextToken = userAccountRepository.findByEmail(userAccount.getEmail()).get();

        // when
        ResultActions finishProcessActions = mockMvc.perform(authenticatedWithAccessToken(
                prepareGet("/user-accounts/current"),
                userAccountWithNextToken.getNextAccessToken()));

        // then
        finishProcessActions
                .andExpect(jsonPath("$.email").value(userAccount.getEmail()))
                .andExpect(jsonPath("$.username").value(userAccount.getUsername()))
                .andExpect(jsonPath("$.roles.length()").value(1))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"));
    }
}
