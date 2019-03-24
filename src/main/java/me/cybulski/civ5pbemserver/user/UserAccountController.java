package me.cybulski.civ5pbemserver.user;

import com.google.common.collect.ImmutableSet;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.cybulski.civ5pbemserver.user.dto.CurrentUserOutputDTO;
import me.cybulski.civ5pbemserver.user.dto.RegisterInputDTO;
import me.cybulski.civ5pbemserver.user.dto.RegisterOutputDTO;
import me.cybulski.civ5pbemserver.user.dto.ResetAccessTokenDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static me.cybulski.civ5pbemserver.config.SecurityConstants.ANONYMOUS_USER;
import static me.cybulski.civ5pbemserver.config.SecurityConstants.ROLE_ANONYMOUS;

/**
 * @author Michał Cybulski
 */
@Transactional(readOnly = true)
@RestController
@RequestMapping("user-accounts")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class UserAccountController {

    private final UserAccountApplicationService userAccountApplicationService;

    @Transactional
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

    @Transactional
    @RequestMapping(path = "reset-access-token", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity resetAccessToken(@RequestBody @Validated ResetAccessTokenDTO resetAccessTokenDTO) {
        userAccountApplicationService.startResetTokenProcess(resetAccessTokenDTO.getEmail());
        return ResponseEntity.ok().build();
    }

    @RequestMapping(path = "current", method = RequestMethod.GET)
    public CurrentUserOutputDTO currentUser() {
        return userAccountApplicationService.getCurrentUserAccount()
                .map(userAccount -> CurrentUserOutputDTO.builder()
                        .email(userAccount.getEmail())
                        .username(userAccount.getUsername())
                        .roles(userAccount.getRoles())
                        .build())
                .orElseGet(() -> CurrentUserOutputDTO.builder()
                        .email(ANONYMOUS_USER)
                        .username(ANONYMOUS_USER)
                        .roles(ImmutableSet.of(ROLE_ANONYMOUS))
                        .build());
    }
}
