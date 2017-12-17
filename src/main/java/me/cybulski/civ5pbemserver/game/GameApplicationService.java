package me.cybulski.civ5pbemserver.game;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.cybulski.civ5pbemserver.game.dto.NewGameInputDTO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

/**
 * @author Michał Cybulski
 */
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class GameApplicationService {

    private final GameFactory gameFactory;
    private final GameRepository gameRepository;

    @PreAuthorize("hasRole('USER')")
    public Game createNewGame(NewGameInputDTO newGameInputDTO) {
        Game newGame = gameFactory.createNewGame(newGameInputDTO);
        gameRepository.save(newGame);

        return newGame;
    }
}
