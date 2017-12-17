package me.cybulski.civ5pbemserver.user;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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

    public UserAccount registerUserAccount(@NonNull String email, @NonNull String password) {
        return userAccountRepository.save(userAccountFactory.createUserAccount(email, password));
    }

    public Optional<UserAccount> findUserByEmail(@NonNull String email) {
        return userAccountRepository.findByEmail(email);
    }

    public Optional<UserAccount> findUserByToken(@NonNull String accessToken) {
        return userAccountRepository.findByCurrentAccessToken(accessToken);
    }
}
