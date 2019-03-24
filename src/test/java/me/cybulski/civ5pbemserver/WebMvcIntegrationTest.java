package me.cybulski.civ5pbemserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.cybulski.civ5pbemserver.user.UserAccount;
import me.cybulski.civ5pbemserver.user.UserAccountApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.URI;

/**
 * @author Michał Cybulski
 */
@AutoConfigureMockMvc
public abstract class WebMvcIntegrationTest extends IntegrationTest {

    private static final String APPLICATION_JSON = "application/json";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UserAccountApplicationService userAccountApplicationService;

    protected MockHttpServletRequestBuilder prepareGet(String url) {
        return MockMvcRequestBuilders
                       .get(URI.create(url))
                       .contentType(APPLICATION_JSON);
    }

    protected MockHttpServletRequestBuilder preparePost(String url) {
        return MockMvcRequestBuilders
                .post(URI.create(url));
    }

    protected MockHttpServletRequestBuilder preparePost(String url, Object data) throws JsonProcessingException {
        return MockMvcRequestBuilders
                       .post(URI.create(url))
                       .content(objectMapper.writeValueAsString(data))
                       .contentType(APPLICATION_JSON);
    }

    protected MockHttpServletRequestBuilder authenticated(MockHttpServletRequestBuilder builder,
                                                          UserAccount userAccount) {
        return builder.header("Access-Token", userAccount.getCurrentAccessToken());
    }

    protected MockHttpServletRequestBuilder authenticatedWithAccessToken(MockHttpServletRequestBuilder builder,
                                                                         String accessToken) {
        return builder.header("Access-Token", accessToken);
    }
}
