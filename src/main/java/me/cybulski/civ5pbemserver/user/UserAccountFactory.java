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

    UserAccount createUserAccount(String email) {
        return UserAccount.builder()
                       .email(email)
                       .username(email)
                       .currentAccessToken(UUID.randomUUID().toString())
                       .build();
    }
}
