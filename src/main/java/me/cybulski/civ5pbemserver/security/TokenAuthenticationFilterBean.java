package me.cybulski.civ5pbemserver.security;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Michał Cybulski
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class TokenAuthenticationFilterBean extends AbstractPreAuthenticatedProcessingFilter {

    private static final String ACCESS_TOKEN_HEADER = "Access-Token";

    private final DefaultUserDetailsService defaultUserDetailsService;

    private UserDetails getUserDetails(String accessToken) {
        return defaultUserDetailsService.loadUserByAccessToken(accessToken);
    }

    private String getCredentials(HttpServletRequest httpRequest) {
        return httpRequest.getHeader(ACCESS_TOKEN_HEADER);
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        String credentials = getCredentials(request);

        return credentials != null ? getUserDetails(credentials) : null;
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return getCredentials(request);
    }
}
