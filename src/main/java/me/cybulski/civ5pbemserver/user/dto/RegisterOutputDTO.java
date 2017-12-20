package me.cybulski.civ5pbemserver.user.dto;

import lombok.*;

/**
 * @author Michał Cybulski
 */
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class RegisterOutputDTO {
    private String email;
    private String username;
}
