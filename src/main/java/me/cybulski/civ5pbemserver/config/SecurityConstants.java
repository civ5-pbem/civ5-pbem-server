package me.cybulski.civ5pbemserver.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Michał Cybulski
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecurityConstants {
    public static final String HAS_ROLE_USER = "hasRole('USER')";
    public static final String ANONYMOUS_USER = "anonymousUser";
    public static final String ROLE_ANONYMOUS = "ROLE_ANONYMOUS";
}
