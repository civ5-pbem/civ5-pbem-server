package me.cybulski.civ5pbemserver.user;

import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

/**
 * @author Michał Cybulski
 */
public class TestUserAccountFactory {

    public UsernameStep instance() {
        return new Factory();
    }

    public interface UsernameStep {
        EmailStep withDefaultUsername();
        EmailStep withUsername(String username);
    }

    public interface EmailStep {
        RegistrationConfirmationStep withDefaultEmail();
        RegistrationConfirmationStep withEmail(String email);
    }

    public interface RegistrationConfirmationStep {
        RegistrationConfirmedStep withConfirmedRegistration();
        BuildStep withNotConfirmedRegistration();
    }

    public interface RegistrationConfirmedStep {
        RegistrationConfirmedStep resetTokenProcessStarted();

        BuildStep toBuildStep();
    }

    public interface BuildStep {
        UserAccount build();
        UserAccount build(TestEntityManager entityManager);
    }

    public static class Factory implements
            UsernameStep,
            EmailStep,
            RegistrationConfirmationStep,
            RegistrationConfirmedStep,
            BuildStep {

        private String email = "michal@cybulski.me";
        private String username = "mcybulsk";

        private boolean registrationConfirmed = false;
        private boolean resetTokenProcessStarted = false;

        private UserAccountFactory userAccountFactory = new UserAccountFactory();

        @Override
        public EmailStep withDefaultUsername() {
            return this;
        }

        @Override
        public EmailStep withUsername(String username) {
            this.username = username;
            return this;
        }

        @Override
        public RegistrationConfirmationStep withDefaultEmail() {
            return this;
        }

        @Override
        public RegistrationConfirmationStep withEmail(String email) {
            this.email = email;
            return this;
        }

        @Override
        public RegistrationConfirmedStep withConfirmedRegistration() {
            this.registrationConfirmed = true;
            return this;
        }

        @Override
        public BuildStep withNotConfirmedRegistration() {
            return this;
        }

        @Override
        public RegistrationConfirmedStep resetTokenProcessStarted() {
            this.resetTokenProcessStarted = true;
            return this;
        }

        @Override
        public BuildStep toBuildStep() {
            return this;
        }

        @Override
        public UserAccount build() {
            UserAccount userAccount = userAccountFactory.createUserAccount(this.email, this.username);
            if (registrationConfirmed) {
                userAccount.confirmRegistration();
            }
            if (resetTokenProcessStarted) {
                userAccount.startResetToken();
            }
            return userAccount;
        }

        @Override
        public UserAccount build(TestEntityManager entityManager) {
            UserAccount userAccount = build();
            return entityManager.persistAndFlush(userAccount);
        }
    }
}
