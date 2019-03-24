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
        mailSender.send(prepareMail(
                emailAddress,
                "[Civ 5] Civ 5 play by email registration confirmation",
                "Hello,\n\n" +
                        "your access token: " + accessToken + "\n" +
                        "If you didn't request it, then just please ignore this message.\n"));
    }

    @Override
    public void sendSaveGameValidationDisabledEmail(String emailAddress, String gameName) {
        mailSender.send(prepareMail(
                emailAddress,
                "[Civ 5] Host just disabled save game validation for a hot-seat meeting",
                "Hello,\n\n" +
                        "host of the game " + gameName + " just disabled save game validations for one turn.\n" +
                        "If this was not agreed upon, then please take action to resolve the issue with the host.\n"));
    }

    @Override
    public void sendYourTurnEmail(String emailAddress, String gameName) {
        mailSender.send(prepareMail(
                emailAddress,
                "[Civ 5] Game is waiting for your turn",
                "Hello,\n\n" +
                        "it's now your turn in the game: " + gameName + ".\n"));
    }

    @Override
    public void sendYouWereKickedEmail(String emailAddress, String gameName) {
        mailSender.send(prepareMail(
                emailAddress,
                "[Civ 5] Host just kicked you out of the game",
                "Hello,\n\n" +
                        "host of the game " + gameName + " just kicked you from it.\n"));
    }

    @Override
    public void sendGameJustStarted(String emailAddress, String gameName) {
        mailSender.send(prepareMail(
                emailAddress,
                "[Civ 5] Game just started",
                "Hello,\n\n" +
                        "host of the game " + gameName + " just started the game.\n" +
                        "You will be notified when it's your turn.\n"));
    }

    @Override
    public void sendResetTokenEmail(String emailAddress, String nextAccessToken) {
        mailSender.send(prepareMail(
                emailAddress,
                "[Civ 5] Reset access token process started",
                "Hello,\n\n" +
                        "it appears that you have requested a new access token: " + nextAccessToken + "\n" +
                        "If you didn't request it, then just please ignore this message.\n"));
    }

    @Override
    public void confirmResetTokenEmail(String emailAddress) {
        mailSender.send(prepareMail(
                emailAddress,
                "[Civ 5] Reset access token process finished",
                "Hello,\n\n" +
                        "you have successfully finished the reset password process.\n" +
                        "Please use the new token from now on.\n"));
    }

    protected SimpleMailMessage prepareMail(String emailAddress, String subject, String mailContent) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(emailAddress);
        simpleMailMessage.setFrom(environment.getProperty("civ5.mail.from"));
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(mailContent);
        return simpleMailMessage;
    }
}
