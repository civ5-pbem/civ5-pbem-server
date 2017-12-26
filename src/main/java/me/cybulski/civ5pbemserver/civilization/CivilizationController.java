package me.cybulski.civ5pbemserver.civilization;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.cybulski.civ5pbemserver.civilization.dto.CivilizationDTO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Krzysztof Cybulski
 */
@RestController
@RequestMapping("civilizations")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class CivilizationController {

    private final CivilizationApplicationService civilizationApplicationService;

    @RequestMapping(path = "", method = RequestMethod.GET)
    List<CivilizationDTO> listAllCivilizations(){
        return civilizationApplicationService.getAllCivilizations();
    }

}
