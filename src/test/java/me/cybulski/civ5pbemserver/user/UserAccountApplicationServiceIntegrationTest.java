package me.cybulski.civ5pbemserver.user;

import me.cybulski.civ5pbemserver.IntegrationTest;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * @author Michał Cybulski
 */
public class UserAccountApplicationServiceIntegrationTest extends IntegrationTest {

    @Autowired
    private UserAccountApplicationService userAccountApplicationService;

    @Autowired
    private UserAccountRepository userAccountRepository;

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
        verify(mailService).sendRegistrationConfirmationEmail(email, newUserAccount.get().getCurrentAccessToken());
    }

    @Test
    public void userCanStartResetTokenProcess() {
        // given
        UserAccount originalUserAccount = new TestUserAccountFactory().instance()
                .withDefaultUsername()
                .withDefaultEmail()
                .withConfirmedRegistration()
                .toBuildStep().build(testEntityManager);

        // and
        String email = originalUserAccount.getEmail();

        // and
        String originalAccessToken = originalUserAccount.getCurrentAccessToken();

        // when
        userAccountApplicationService.startResetTokenProcess(email);

        // then
        Optional<UserAccount> userAccountAfterProcessOptional = userAccountRepository.findByEmail(email);
        Assertions.assertThat(userAccountAfterProcessOptional).isPresent();
        UserAccount userAccount = userAccountAfterProcessOptional.get();
        assertThat(userAccount.getCurrentAccessToken()).isEqualTo(originalAccessToken);
        assertThat(userAccount.getNextAccessToken()).isNotEmpty();
        assertThat(userAccount.getNextAccessToken()).isNotEqualTo(originalAccessToken);

        // and
        verify(mailService).sendResetTokenEmail(userAccount.getEmail(), userAccount.getNextAccessToken());
    }

    @Test
    public void userCanStartResetTokenProcessAgainToResendEmail() {
        // given
        UserAccount originalUserAccount = new TestUserAccountFactory().instance()
                .withDefaultUsername()
                .withDefaultEmail()
                .withConfirmedRegistration()
                .resetTokenProcessStarted()
                .toBuildStep().build(testEntityManager);

        // and
        String email = originalUserAccount.getEmail();

        // and
        String originalAccessToken = originalUserAccount.getCurrentAccessToken();
        String originalNewAccessToken = originalUserAccount.getNextAccessToken();

        // when
        userAccountApplicationService.startResetTokenProcess(email);

        // then
        Optional<UserAccount> userAccountAfterProcessOptional = userAccountRepository.findByEmail(email);
        Assertions.assertThat(userAccountAfterProcessOptional).isPresent();
        UserAccount userAccount = userAccountAfterProcessOptional.get();
        assertThat(userAccount.getCurrentAccessToken()).isEqualTo(originalAccessToken);
        assertThat(userAccount.getNextAccessToken()).isNotEmpty();
        assertThat(userAccount.getNextAccessToken()).isNotEqualTo(originalNewAccessToken);

        // and
        verify(mailService).sendResetTokenEmail(userAccount.getEmail(), userAccount.getNextAccessToken());
    }

    @Test
    public void userCanFinishResetTokenProcess() {
        // given
        UserAccount userAccount = new TestUserAccountFactory().instance()
                .withDefaultUsername()
                .withDefaultEmail()
                .withConfirmedRegistration()
                .resetTokenProcessStarted()
                .toBuildStep().build(testEntityManager);

        // and
        String email = userAccount.getEmail();

        // and
        String originalNewAccessToken = userAccount.getNextAccessToken();

        // when
        userAccountApplicationService.finishResetTokenProcess(originalNewAccessToken);

        // then
        Optional<UserAccount> userAccountAfterProcessOptional = userAccountRepository.findByEmail(email);
        Assertions.assertThat(userAccountAfterProcessOptional).isPresent();
        UserAccount userAccountAfterProcess = userAccountAfterProcessOptional.get();
        assertThat(userAccountAfterProcess.getCurrentAccessToken()).isEqualTo(originalNewAccessToken);
        assertThat(userAccountAfterProcess.getNextAccessToken()).isNullOrEmpty();

        // and
        verify(mailService).confirmResetTokenEmail(email);
    }

    @Test
    public void userCannotFinishResetTokenProcessWithWrongToken() {
        // given
        UserAccount userAccount = new TestUserAccountFactory().instance()
                .withDefaultUsername()
                .withDefaultEmail()
                .withConfirmedRegistration()
                .resetTokenProcessStarted()
                .toBuildStep().build(testEntityManager);

        // and
        String email = userAccount.getEmail();

        // and
        String originalAccessToken = userAccount.getCurrentAccessToken();
        String originalNewAccessToken = userAccount.getNextAccessToken();

        // when
        userAccountApplicationService.finishResetTokenProcess(UUID.randomUUID().toString());

        // then
        Optional<UserAccount> userAccountAfterProcessOptional = userAccountRepository.findByEmail(email);
        Assertions.assertThat(userAccountAfterProcessOptional).isPresent();
        UserAccount userAccountAfterProcess = userAccountAfterProcessOptional.get();
        assertThat(userAccountAfterProcess.getCurrentAccessToken()).isEqualTo(originalAccessToken);
        assertThat(userAccountAfterProcess.getNextAccessToken()).isEqualTo(originalNewAccessToken);

        // and
        verifyNoMoreInteractions(mailService);
    }
}
