package me.cybulski.civ5pbemserver.mail;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

/**
 * @author Michał Cybulski
 */
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DefaultMailService implements MailService {

    private final MailSender mailSender;
    private final Environment environment;

    @Override
    public void sendRegistrationConfirmationEmail(String emailAddress, String accessToken) {
        mailSender.send(prepareRegistrationMailMessage(emailAddress, accessToken));
    }

    protected SimpleMailMessage prepareRegistrationMailMessage(String emailAddress, String accessToken) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(emailAddress);
        simpleMailMessage.setFrom(environment.getProperty("civ5.mail.from"));
        simpleMailMessage.setSubject("Civ 5 play by email registration confirmation");
        simpleMailMessage.setText("Hello,\n\n" +
                                          "your access token: " + accessToken + "\n" +
                                          "If you didn't request it, then just please ignore this message.\n");
        return simpleMailMessage;
    }
}
