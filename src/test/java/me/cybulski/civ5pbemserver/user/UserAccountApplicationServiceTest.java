package me.cybulski.civ5pbemserver.user;

import me.cybulski.civ5pbemserver.IntegrationTest;
import me.cybulski.civ5pbemserver.mail.MailService;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

/**
 * @author Michał Cybulski
 */
public class UserAccountApplicationServiceTest extends IntegrationTest {

    @Autowired
    private UserAccountApplicationService userAccountApplicationService;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @SpyBean
    private MailService spyMailService;

    @Test
    public void registerUserAccountWorks() throws Exception {
        // given
        String email = "michal@cybulski.me";
        String username = "mcybulsk";

        // when
        userAccountApplicationService.registerUserAccount(email, username);

        // then
        Optional<UserAccount> newUserAccount = userAccountRepository.findByEmail(email);
        Assertions.assertThat(newUserAccount).isPresent();
        assertThat(newUserAccount.map(UserAccount::getEmail)).contains(email);
        assertThat(newUserAccount.map(UserAccount::getCurrentAccessToken)).isPresent();
        assertThat(newUserAccount.map(UserAccount::isRegistrationConfirmed)).contains(false);

        // and
        verify(spyMailService).sendRegistrationConfirmationEmail(email, newUserAccount.get().getCurrentAccessToken());
    }
}
