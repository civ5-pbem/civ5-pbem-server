package me.cybulski.civ5pbemserver.saveparser;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * @author Michał Cybulski
 */
public class SaveGameDTOTestCreator {

    public SaveGameDTO prepareValidSaveGameDTOWith4Players() {
        SaveGamePlayerDTO saveGamePlayer1 = SaveGamePlayerDTO.builder()
                                                    .playerNumber(0)
                                                    .playerStatus(SaveGamePlayerStatus.HUMAN)
                                                    .password("")
                                                    .build();
        SaveGamePlayerDTO saveGamePlayer2 = SaveGamePlayerDTO.builder()
                                                    .playerNumber(1)
                                                    .playerStatus(SaveGamePlayerStatus.DEAD)
                                                    .password("")
                                                    .build();
        SaveGamePlayerDTO saveGamePlayer3 = SaveGamePlayerDTO.builder()
                                                    .playerNumber(2)
                                                    .playerStatus(SaveGamePlayerStatus.AI)
                                                    .password("")
                                                    .build();
        SaveGamePlayerDTO saveGamePlayer4 = SaveGamePlayerDTO.builder()
                                                    .playerNumber(3)
                                                    .playerStatus(SaveGamePlayerStatus.MISSING)
                                                    .password("")
                                                    .build();
        List<SaveGamePlayerDTO> saveGamePlayers =
                ImmutableList.of(saveGamePlayer1, saveGamePlayer2, saveGamePlayer3, saveGamePlayer4);

        return SaveGameDTO.builder()
                       .gameVersion("version")
                       .turnNumber(15)
                       .players(saveGamePlayers)
                       .playerWhoMoves(saveGamePlayer1)
                       .build();
    }
}
