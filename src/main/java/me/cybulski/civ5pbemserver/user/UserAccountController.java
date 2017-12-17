package me.cybulski.civ5pbemserver.user;

import me.cybulski.civ5pbemserver.user.dto.CurrentUserOutputDTO;
import me.cybulski.civ5pbemserver.user.dto.RegisterInputDTO;
import me.cybulski.civ5pbemserver.user.dto.RegisterOutputDTO;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

/**
 * @author Michał Cybulski
 */
@RestController
@RequestMapping("user-accounts")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class UserAccountController {

    private final UserAccountApplicationService userAccountApplicationService;

    @RequestMapping(path = "register", method = RequestMethod.POST, consumes = "application/json")
    public RegisterOutputDTO register(@RequestBody @Validated RegisterInputDTO registerInputDTO) {
        UserAccount newUserAccount = userAccountApplicationService.registerUserAccount(
                registerInputDTO.getEmail());

        return RegisterOutputDTO.builder()
                       .email(newUserAccount.getEmail())
                       .build();
    }

    @RequestMapping(path = "current", method = RequestMethod.GET)
    public CurrentUserOutputDTO currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return CurrentUserOutputDTO.builder()
                       .email(authentication.getName())
                       .username(authentication.getName())
                       .roles(authentication.getAuthorities().stream()
                                      .map(Object::toString)
                                      .collect(Collectors.toSet()))
                       .build();
    }
}
