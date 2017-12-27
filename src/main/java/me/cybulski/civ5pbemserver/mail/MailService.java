package me.cybulski.civ5pbemserver.mail;

/**
 * @author Michał Cybulski
 */
public interface MailService {

    void sendRegistrationConfirmationEmail(String emailAddress, String accessToken);

    void sendSaveGameValidationDisabledEmail(String emailAddress, String gameName);

    void sendYourTurnEmail(String emailAddress, String gameName);

    void sendYouWereKickedEmail(String emailAddress, String gameName);

    void sendGameJustStarted(String emailAddress, String gameName);
}
