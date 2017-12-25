package me.cybulski.civ5pbemserver.game;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Michał Cybulski
 */
public interface GameRepository extends JpaRepository<Game, String> {
}
