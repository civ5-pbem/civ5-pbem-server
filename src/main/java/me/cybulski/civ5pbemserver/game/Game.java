package me.cybulski.civ5pbemserver.game;

import lombok.*;
import me.cybulski.civ5pbemserver.jpa.BaseEntity;
import me.cybulski.civ5pbemserver.user.UserAccount;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

/**
 * @author Michał Cybulski
 */
// FIXME add schema migration
// FIXME add tests
@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
class Game extends BaseEntity {

    @ManyToOne(optional = false)
    private UserAccount host;

    @NotNull
    private String name;

    @Size(max = 1024)
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    private MapSize mapSize;

    @NotNull
    @Enumerated(EnumType.STRING)
    private GameState gameState;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    private Set<Player> players;

    @NotNull
    @Min(0)
    @Max(12)
    private Integer maxNumberOfPlayers;

    @NotNull
    @Min(0)
    @Max(58)
    private Integer numberOfCityStates;
}
