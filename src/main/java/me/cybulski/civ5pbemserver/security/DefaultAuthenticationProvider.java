package me.cybulski.civ5pbemserver.security;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.cybulski.civ5pbemserver.user.UserAccountApplicationService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Michał Cybulski
 */
@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Transactional(readOnly = true)
public class DefaultAuthenticationProvider implements AuthenticationProvider {

    private final UserAccountApplicationService userAccountApplicationService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Optional<Authentication> validAuthentication = Optional.empty();

        Object potentialUser = authentication.getPrincipal();
        if (potentialUser != null && User.class.isAssignableFrom(potentialUser.getClass())) {
            User user = (User) potentialUser;
            String accessToken = user.getPassword();

            if (accessToken != null) {
                validAuthentication =
                        userAccountApplicationService.findUserByToken(accessToken)
                                .filter(userAccount -> userAccount.getEmail().equals(user.getUsername()))
                                .map(userAccount -> userAccount.getRoles().stream()
                                        .map(SimpleGrantedAuthority::new)
                                        .collect(Collectors.toList()))
                                .map(roles -> new PreAuthenticatedAuthenticationToken(user, accessToken, roles));
                validAuthentication.ifPresent(auth -> auth.setAuthenticated(true));
            }
        }

        return validAuthentication.orElse(authentication);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return PreAuthenticatedAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
