package me.cybulski.civ5pbemserver.game;

import lombok.*;
import me.cybulski.civ5pbemserver.jpa.BaseEntity;
import me.cybulski.civ5pbemserver.user.UserAccount;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * @author Michał Cybulski
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "game_id", "playerNumber" }))
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
class Player extends BaseEntity {

    @NotNull
    @ManyToOne(optional = false)
    private Game game;

    private int playerNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Civilization civilization;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PlayerType playerType;

    @ManyToOne
    private UserAccount humanUserAccount;

    void changeToHuman() {
        // FIXME test me
        this.playerType = PlayerType.HUMAN;
    }

    void changeToAi() {
        // FIXME test me
        this.playerType = PlayerType.AI;
    }

    void close() {
        // FIXME test me
        this.playerType = PlayerType.CLOSED;
    }

    void joinHuman(UserAccount newPlayer) {
        // FIXME add HUMAN type check
        this.humanUserAccount = newPlayer;
    }
}
