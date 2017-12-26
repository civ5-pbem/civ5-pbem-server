package me.cybulski.civ5pbemserver.civilization;

import me.cybulski.civ5pbemserver.WebMvcIntegrationTest;
import me.cybulski.civ5pbemserver.user.TestUserAccountFactory;
import me.cybulski.civ5pbemserver.user.UserAccount;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * @author Krzysztof Cybulski
 */
public class CivilizationControllerWebMvcTest extends WebMvcIntegrationTest{

    private final TestUserAccountFactory testUserAccountFactory = new TestUserAccountFactory();
    private UserAccount sampleUserAccount;

    @Before
    public void setUp() {
        // setting up a user
        sampleUserAccount = testUserAccountFactory.createNewUserAccount("sample@test.com", "sampleUser");
        testEntityManager.persistAndFlush(sampleUserAccount);
    }

    @Test
    public void whenUserRequestsCivilizations_thenCivilizationsAreReturned() throws Exception {
        // when
        ResultActions resultActions =
                mockMvc.perform(authenticated(prepareGet("/civilizations"), sampleUserAccount));

        // then
        resultActions.andExpect(status().is(200))
                .andExpect(jsonPath("$.length()").value(44))
                .andExpect(jsonPath("$.[0].code").value("AMERICAN"))
                .andExpect(jsonPath("$.[0].name").value("American"))
                .andExpect(jsonPath("$.[0].leader").value("Washington"));
    }
}