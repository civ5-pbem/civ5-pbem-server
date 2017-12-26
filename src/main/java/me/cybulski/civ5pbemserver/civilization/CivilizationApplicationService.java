package me.cybulski.civ5pbemserver.civilization;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.cybulski.civ5pbemserver.civilization.dto.CivilizationDTO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static me.cybulski.civ5pbemserver.config.SecurityConstants.HAS_ROLE_USER;

/**
 * @author Krzysztof Cybulski
 */
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class CivilizationApplicationService {

    @PreAuthorize(HAS_ROLE_USER)
    public List<CivilizationDTO> getAllCivilizations() {
        return Arrays.stream(Civilization.values())
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private CivilizationDTO convertToDTO(Civilization civilization) {
        return CivilizationDTO.builder()
                .code(civilization.toString())
                .name(civilization.getName())
                .leader(civilization.getLeader())
                .build();
    }
}
