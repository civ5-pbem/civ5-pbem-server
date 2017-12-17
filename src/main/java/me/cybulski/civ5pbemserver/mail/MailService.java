package me.cybulski.civ5pbemserver.mail;

/**
 * @author Michał Cybulski
 */
public interface MailService {

    void sendRegistrationConfirmationEmail(String emailAddress, String accessToken);
}
