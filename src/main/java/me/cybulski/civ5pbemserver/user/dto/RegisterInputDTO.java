package me.cybulski.civ5pbemserver.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * @author Michał Cybulski
 */
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class RegisterInputDTO {

    @NotNull
    @Email
    private String email;

    @NotNull
    private String username;
}
