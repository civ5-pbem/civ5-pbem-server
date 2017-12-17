package me.cybulski.civ5pbemserver.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @author Michał Cybulski
 */
@Component
class DefaultPasswordEncoder extends BCryptPasswordEncoder {
}
