package me.cybulski.civ5pbemserver.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static me.cybulski.civ5pbemserver.config.Profiles.OUTGOING_EMAILS;

/**
 * @author Michał Cybulski
 */
@Service
@Profile("!" + OUTGOING_EMAILS)
@Slf4j
public class MockMailSender implements MailSender {

    @Override
    public void send(SimpleMailMessage simpleMessage) throws MailException {
        log.info("Sending message: {}", simpleMessage);
    }

    @Override
    public void send(SimpleMailMessage... simpleMessages) throws MailException {
        Arrays.stream(simpleMessages).forEach(this::send);
    }
}
