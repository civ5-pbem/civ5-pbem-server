package me.cybulski.civ5pbemserver.user;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.cybulski.civ5pbemserver.mail.MailService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Michał Cybulski
 */
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class UserAccountApplicationService {

    private final UserAccountFactory userAccountFactory;
    private final UserAccountRepository userAccountRepository;
    private final MailService mailService;

    public UserAccount registerUserAccount(@NonNull String email, String username) {
        UserAccount newUserAccount = userAccountRepository.save(userAccountFactory.createUserAccount(email, username));
        mailService.sendRegistrationConfirmationEmail(newUserAccount.getEmail(), newUserAccount.getCurrentAccessToken());

        return newUserAccount;
    }

    public Optional<UserAccount> findUserByEmail(@NonNull String email) {
        return userAccountRepository.findByEmail(email);
    }

    public Optional<UserAccount> findUserByToken(@NonNull String accessToken) {
        return userAccountRepository.findByCurrentAccessToken(accessToken)
                       .map(UserAccount::confirmRegistration);
    }

    public Optional<UserAccount> getCurrentUserAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (User.class.isAssignableFrom(principal.getClass())) {
            User user = (User) principal;

            return findUserByEmail(user.getUsername());
        }

        return Optional.empty();
    }
}