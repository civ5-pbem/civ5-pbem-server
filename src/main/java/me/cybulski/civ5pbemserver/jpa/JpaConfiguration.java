package me.cybulski.civ5pbemserver.jpa;

import me.cybulski.civ5pbemserver.Civ5PbemServerApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author Michał Cybulski
 */
@Configuration
@EnableJpaRepositories(basePackageClasses = Civ5PbemServerApplication.class)
@EnableJpaAuditing
public class JpaConfiguration {
}
