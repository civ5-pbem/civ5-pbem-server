package me.cybulski.civ5pbemserver.security;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author Michał Cybulski
 */
@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Order(Ordered.HIGHEST_PRECEDENCE)
class TokenAuthenticationFilterBean extends GenericFilterBean {

    private static final String ACCESS_TOKEN_HEADER = "Access-Token";

    private final DefaultUserDetailsService defaultUserDetailsService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String accessToken = httpRequest.getHeader(ACCESS_TOKEN_HEADER);

        if (accessToken != null) {
            UserDetails userDetails = defaultUserDetailsService.loadUserByAccessToken(accessToken);

            final UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, accessToken, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }
}
