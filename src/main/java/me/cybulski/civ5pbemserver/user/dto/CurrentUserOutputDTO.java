package me.cybulski.civ5pbemserver.user.dto;

import lombok.*;

import java.util.Set;

/**
 * @author Michał Cybulski
 */
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CurrentUserOutputDTO {
    private String email;
    private String username;
    private Set<String> roles;
}
