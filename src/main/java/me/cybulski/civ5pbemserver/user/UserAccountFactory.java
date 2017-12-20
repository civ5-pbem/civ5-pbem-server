package me.cybulski.civ5pbemserver.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author Michał Cybulski
 */
@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class UserAccountFactory {

    UserAccount createUserAccount(String email, String username) {
        return UserAccount.builder()
                       .email(email)
                       .username(username)
                       .currentAccessToken(UUID.randomUUID().toString())
                       .build();
    }
}
