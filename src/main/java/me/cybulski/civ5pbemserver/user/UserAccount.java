package me.cybulski.civ5pbemserver.user;

import lombok.*;
import me.cybulski.civ5pbemserver.jpa.BaseEntity;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

/**
 * @author Michał Cybulski
 */
@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserAccount extends BaseEntity {

    @NotNull
    @Column(unique = true)
    @Email
    @Getter
    private String email;

    @NotNull
    @Column(unique = true)
    @Getter
    private String username;

    @Getter
    @ElementCollection
    private Set<String> roles;

    @NotNull
    @Column(unique = true)
    @Getter
    private String currentAccessToken;

    @Column(unique = true)
    @Getter
    private String nextAccessToken;

    @Getter
    private boolean registrationConfirmed;

    UserAccount confirmRegistration() {
        registrationConfirmed = true;
        return this;
    }

    void startResetToken() {
        nextAccessToken = UUID.randomUUID().toString();
    }

    void finishResetToken() {
        currentAccessToken = nextAccessToken;
        nextAccessToken = null;
    }
}
