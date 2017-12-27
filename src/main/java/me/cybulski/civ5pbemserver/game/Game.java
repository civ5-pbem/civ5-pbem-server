package me.cybulski.civ5pbemserver.game;

import lombok.*;
import me.cybulski.civ5pbemserver.civilization.Civilization;
import me.cybulski.civ5pbemserver.exception.ResourceNotFoundException;
import me.cybulski.civ5pbemserver.game.exception.CannotJoinGameException;
import me.cybulski.civ5pbemserver.game.exception.CannotModifyGameException;
import me.cybulski.civ5pbemserver.game.exception.CannotStartGameException;
import me.cybulski.civ5pbemserver.jpa.BaseEntity;
import me.cybulski.civ5pbemserver.user.UserAccount;
import org.springframework.util.Assert;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Michał Cybulski
 */
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

    @Getter(AccessLevel.PACKAGE)
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Player> players;

    @NotNull
    @Min(0)
    @Max(58)
    private Integer numberOfCityStates;

    @Getter(AccessLevel.NONE)
    @OneToOne(cascade = CascadeType.ALL)
    private GameTurn currentGameTurn;

    @NotNull
    @Getter(AccessLevel.PACKAGE)
    private Boolean shouldSaveGameFilesBeValidated;

    public List<Player> getPlayerList() {
        return players.stream()
                .sorted(Comparator.comparingInt(Player::getPlayerNumber))
                .collect(Collectors.toList());
    }

    void joinGame(UserAccount userAccount) {
        checkIsGameWaitingForPlayers();
        if (getPlayerList().stream()
                .map(Player::getHumanUserAccount)
                .anyMatch(userAccount::equals)) {
            throw new CannotJoinGameException("Player already joined the game!");
        }

        Player firstEmptyPlayer = getPlayerList().stream()
                .filter(player -> PlayerType.HUMAN.equals(player.getPlayerType()))
                .filter(player -> player.getHumanUserAccount() == null)
                .findFirst()
                .orElseThrow(() -> new CannotJoinGameException("No more Human places left to join!"));

        firstEmptyPlayer.joinHuman(userAccount);
    }

    void changePlayerType(String playerId, PlayerType playerType) {
        checkIsGameWaitingForPlayers();
        Player foundPlayer = findPlayer(playerId)
                .orElseThrow(ResourceNotFoundException::new);

        switch (playerType) {
            case HUMAN:
                foundPlayer.changeToHuman();
                break;
            case AI:
                foundPlayer.changeToAi();
                break;
            case CLOSED:
                foundPlayer.close();
                break;
        }
    }

    Optional<Player> findPlayer(String playerId) {
        return getPlayers().stream()
                .filter(player -> player.getId().equals(playerId))
                .findFirst();
    }

    Optional<Player> findPlayer(UserAccount userAccount) {
        return getPlayers().stream()
                .filter(player -> userAccount.equals(player.getHumanUserAccount()))
                .findAny();
    }

    void startGame() {
        checkIsGameWaitingForPlayers();
        if (!checkAllHumanPlayersJoined()) {
            throw new CannotStartGameException("Not all human players joined!");
        }

        this.gameState = GameState.WAITING_FOR_FIRST_MOVE;
    }

    void chooseCivilization(String playerId, Civilization civilization) {
        checkIsGameWaitingForPlayers();

        Player player = findPlayer(playerId)
                .orElseThrow(ResourceNotFoundException::new);
        player.chooseCivilization(civilization);
    }

    private boolean checkAllHumanPlayersJoined() {
        return getPlayers().stream()
                .filter(player -> PlayerType.HUMAN.equals(player.getPlayerType()))
                .noneMatch(player -> player.getHumanUserAccount() == null);
    }

    void kickPlayer(String playerId) {
        checkIsGameWaitingForPlayers();
        findPlayer(playerId)
                .orElseThrow(ResourceNotFoundException::new)
                .kick();
    }

    void leave(UserAccount currentUser) {
        checkIsGameWaitingForPlayers();
        findPlayer(currentUser).orElseThrow(ResourceNotFoundException::new).leave();
    }

    void nextTurn(GameTurn nextTurn) {
        boolean isFirstMoveBeingFinished = GameState.WAITING_FOR_FIRST_MOVE.equals(gameState);
        Assert.state(GameState.IN_PROGRESS.equals(gameState) || isFirstMoveBeingFinished,
                     "Game must be in IN_PROGRESS or it must be first turn to add next turn");

        if (this.currentGameTurn != null && GameState.WAITING_FOR_FIRST_MOVE.equals(this.gameState)) {
            this.gameState = GameState.IN_PROGRESS;
        }
        this.currentGameTurn = nextTurn;
        shouldSaveGameFilesBeValidated = true;
    }

    void disableValidation() {
        shouldSaveGameFilesBeValidated = false;
    }

    public Optional<GameTurn> getCurrentGameTurn() {
        return Optional.ofNullable(currentGameTurn);
    }

    private void checkIsGameWaitingForPlayers() {
        if (!GameState.WAITING_FOR_PLAYERS.equals(gameState)) {
            throw new CannotModifyGameException("Cannot modify game after it has started");
        }
    }
}
