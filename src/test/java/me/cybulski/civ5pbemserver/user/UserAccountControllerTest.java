package me.cybulski.civ5pbemserver.user;

import me.cybulski.civ5pbemserver.WebMvcIntegrationTest;
import me.cybulski.civ5pbemserver.user.dto.RegisterInputDTO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * @author Michał Cybulski
 */
public class UserAccountControllerTest extends WebMvcIntegrationTest {

    @Autowired
    private UserAccountApplicationService userAccountApplicationService;

    @Test
    public void whenUserRegisters_thenNewUserAccountIsCreated() throws Exception {
        // given
        String email = "some@email.com";
        RegisterInputDTO data = RegisterInputDTO.builder()
                .email(email)
                .password("password")
                .build();

        // when
        ResultActions resultAction = mockMvc.perform(preparePost("/user-accounts/register", data));

        // then
        resultAction
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.accessToken").isString());
    }

    @Test
    public void whenUnauthenticatedUserVisitsCurrent_thenAnonymousUserIsReturned() throws Exception {
        // when
        ResultActions resultActions = mockMvc.perform(prepareGet("/user-accounts/current"));

        // then
        resultActions
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.email").value("anonymousUser"))
                .andExpect(jsonPath("$.username").value("anonymousUser"))
                .andExpect(jsonPath("$.roles.length()").value(1))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_ANONYMOUS"));
    }

    @Test
    public void whenRegisteredUserVisitsCurrent_thenUserIsReturned() throws Exception {
        // given
        String email = "some@email.com";

        // and
        userAccountApplicationService.registerUserAccount(email, "password");

        // when
        ResultActions resultActions = mockMvc.perform(
                authenticated(prepareGet("/user-accounts/current"),
                userAccountApplicationService.findUserByEmail(email).get()));

        // then
        resultActions
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.username").value(email))
                .andExpect(jsonPath("$.roles.length()").value(1))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"));
    }
}
