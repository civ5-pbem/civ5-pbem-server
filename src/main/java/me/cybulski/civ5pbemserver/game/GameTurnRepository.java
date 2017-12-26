package me.cybulski.civ5pbemserver.game;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Michał Cybulski
 */
interface GameTurnRepository extends JpaRepository<GameTurn, String> {
}
