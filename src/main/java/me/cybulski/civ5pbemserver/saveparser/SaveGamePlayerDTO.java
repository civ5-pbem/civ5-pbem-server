package me.cybulski.civ5pbemserver.saveparser;

import lombok.*;

/**
 * @author Michał Cybulski
 */
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter(AccessLevel.PACKAGE)
public class SaveGamePlayerDTO {

    private int playerNumber;
    private String password;
    private SaveGamePlayerStatus playerStatus;
}
