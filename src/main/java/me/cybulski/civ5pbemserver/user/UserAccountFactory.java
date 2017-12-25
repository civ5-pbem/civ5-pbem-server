package me.cybulski.civ5pbemserver.user;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

/**
 * @author Michał Cybulski
 */
@Component
class UserAccountFactory {

    UserAccount createUserAccount(String email, String username) {
        return UserAccount.builder()
                       .email(email)
                       .username(username)
                       .currentAccessToken(UUID.randomUUID().toString())
                       .roles(new HashSet<>(Collections.singletonList("ROLE_USER")))
                       .build();
    }
}
