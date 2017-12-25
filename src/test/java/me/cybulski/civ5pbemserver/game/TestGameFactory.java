package me.cybulski.civ5pbemserver.game;

import me.cybulski.civ5pbemserver.game.dto.NewGameInputDTO;
import me.cybulski.civ5pbemserver.user.UserAccount;

/**
 * @author Michał Cybulski
 */
public class TestGameFactory {

    private PlayerFactory playerFactory;
    private GameFactory gameFactory;

    public TestGameFactory() {
        this.playerFactory = new PlayerFactory();
        this.gameFactory = new GameFactory(playerFactory);
    }

    public TestGameFactory(PlayerFactory playerFactory) {
        this.playerFactory = playerFactory;
        this.gameFactory = new GameFactory(playerFactory);
    }

    private NewGameInputDTO.NewGameInputDTOBuilder newGameInputDTOBuilder = NewGameInputDTO
            .builder()
            .gameName("Game name")
            .gameDescription("Game description")
            .mapSize(MapSize.STANDARD);

    public Game createNewTestGame(UserAccount hostUserAccount) {
        return gameFactory.createNewGame(hostUserAccount, newGameInputDTOBuilder.build());
    }

    public Game createNewTestGame(UserAccount hostUserAccount, MapSize mapSize) {
        return gameFactory.createNewGame(hostUserAccount, newGameInputDTOBuilder.mapSize(mapSize).build());
    }
}
