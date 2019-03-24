package me.cybulski.civ5pbemserver.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Michał Cybulski
 */
interface UserAccountRepository extends JpaRepository<UserAccount, String> {

    Optional<UserAccount> findByEmail(String email);
    Optional<UserAccount> findByCurrentAccessToken(String currentAccessToken);
    Optional<UserAccount> findByNextAccessToken(String nextAccessToken);
}
