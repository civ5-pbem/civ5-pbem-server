package me.cybulski.civ5pbemserver.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ResetAccessTokenDTO {
    @NotNull
    @Email
    private String email;
}
