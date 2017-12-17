package me.cybulski.civ5pbemserver.user;

import me.cybulski.civ5pbemserver.jpa.BaseEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

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

    @NotNull
    @Size(min = 6)
    @Getter
    private String password;

    @ElementCollection
    private Set<String> roles;

    @NotNull
    @Column(unique = true)
    @Getter
    private String currentAccessToken;
}
