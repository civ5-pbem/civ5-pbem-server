package me.cybulski.civ5pbemserver.game;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Michał Cybulski
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum MapSize {
    DUEL(2, 4),
    TINY(4, 8),
    SMALL(6, 12),
    STANDARD(8, 16),
    LARGE(10, 20),
    HUGE(12, 24);

    private final int maxNumberOfPlayers;
    private final int defaultNumberOfCityStates;
}
