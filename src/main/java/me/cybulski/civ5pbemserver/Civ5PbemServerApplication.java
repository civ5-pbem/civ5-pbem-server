package me.cybulski.civ5pbemserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class Civ5PbemServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(Civ5PbemServerApplication.class, args);
    }
}
