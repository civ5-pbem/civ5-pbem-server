package me.cybulski.civ5pbemserver.game;

import lombok.*;
import me.cybulski.civ5pbemserver.jpa.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

/**
 * @author Michał Cybulski
 */
@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
class GameTurn extends BaseEntity {

    @ManyToOne(optional = false)
    private Game game;

    @ManyToOne(optional = false)
    private Player currentPlayer;

    @NotNull
    private Integer turnNumber;

    @OneToOne(fetch = FetchType.LAZY)
    private GameTurn previousGameTurn;

    private String saveFilename;
}
