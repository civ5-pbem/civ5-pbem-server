package me.cybulski.civ5pbemserver.security;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.cybulski.civ5pbemserver.user.UserAccount;
import me.cybulski.civ5pbemserver.user.UserAccountApplicationService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

/**
 * @author Michał Cybulski
 */
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class UserDetailsProvider {

    private final UserAccountApplicationService userAccountApplicationService;

    public UserDetails loadUserByAccessToken(String accessToken) {
        Optional<UserAccount> user = userAccountApplicationService.findUserByToken(accessToken);
        if (!user.isPresent()) {
            user = userAccountApplicationService.finishResetTokenProcess(accessToken);
        }
        return user.map(this::convertToUser)
                   .orElseThrow(() -> new BadCredentialsException("Wrong access token value"));
    }

    private User convertToUser(UserAccount userAccount) {
        ArrayList<SimpleGrantedAuthority> roles = new ArrayList<>();
        userAccount.getRoles().forEach(role -> roles.add(new SimpleGrantedAuthority(role)));

        return new User(userAccount.getEmail(), userAccount.getCurrentAccessToken(), roles);
    }
}
