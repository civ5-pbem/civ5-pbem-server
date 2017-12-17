package me.cybulski.civ5pbemserver.user;

import me.cybulski.civ5pbemserver.IntegrationTest;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michał Cybulski
 */
public class UserAccountApplicationServiceTest extends IntegrationTest {

    @Autowired
    private UserAccountApplicationService userAccountApplicationService;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void registerUserAccount() throws Exception {
        // given
        String email = "some@email.com";
        String password = "somepassword";

        // when
        userAccountApplicationService.registerUserAccount(email, password);

        // then
        Optional<UserAccount> newUserAccount = userAccountRepository.findByEmail(email);
        Assertions.assertThat(newUserAccount).isPresent();
        assertThat(newUserAccount.map(UserAccount::getEmail)).contains(email);
        assertThat(newUserAccount
                           .map(UserAccount::getPassword)
                           .map(encPassword -> passwordEncoder.matches(password, encPassword)))
                .contains(true);
        assertThat(newUserAccount.map(UserAccount::getCurrentAccessToken)).isPresent();
    }

}