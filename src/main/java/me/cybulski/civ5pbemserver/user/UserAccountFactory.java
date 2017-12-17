package me.cybulski.civ5pbemserver.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author Michał Cybulski
 */
@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class UserAccountFactory {

    private final PasswordEncoder passwordEncoder;

    UserAccount createUserAccount(String email, String password) {
        return UserAccount.builder()
                       .email(email)
                       .username(email)
                       .password(passwordEncoder.encode(password))
                       .currentAccessToken(UUID.randomUUID().toString())
                       .build();
    }
}
