package me.cybulski.civ5pbemserver.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.cybulski.civ5pbemserver.exception.ResourceNotFoundException;
import me.cybulski.civ5pbemserver.user.dto.CurrentUserOutputDTO;
import me.cybulski.civ5pbemserver.user.dto.RegisterInputDTO;
import me.cybulski.civ5pbemserver.user.dto.RegisterOutputDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Michał Cybulski
 */
@RestController
@RequestMapping("user-accounts")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class UserAccountController {

    private static final String ANONYMOUS_USER = "anonymousUser";

    private final UserAccountApplicationService userAccountApplicationService;

    @RequestMapping(path = "register", method = RequestMethod.POST, consumes = "application/json")
    public RegisterOutputDTO register(@RequestBody @Validated RegisterInputDTO registerInputDTO) {
        UserAccount newUserAccount = userAccountApplicationService.registerUserAccount(
                registerInputDTO.getEmail(),
                registerInputDTO.getUsername());

        return RegisterOutputDTO.builder()
                       .email(newUserAccount.getEmail())
                       .username(newUserAccount.getUsername())
                       .build();
    }

    @RequestMapping(path = "current", method = RequestMethod.GET)
    public CurrentUserOutputDTO currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Set<String> roles = authentication.getAuthorities().stream()
                                    .map(Object::toString)
                                    .collect(Collectors.toSet());

        if (ANONYMOUS_USER.equals(email)) {
            return CurrentUserOutputDTO.builder()
                           .email(authentication.getName())
                           .username(authentication.getName())
                           .roles(roles)
                           .build();
        }

        return userAccountApplicationService.findUserByEmail(email)
                       .map(userAccount -> CurrentUserOutputDTO.builder()
                                                   .email(userAccount.getEmail())
                                                   .username(userAccount.getUsername())
                                                   .roles(roles)
                                                   .build())
                       .orElseThrow(ResourceNotFoundException::new);
    }
}
