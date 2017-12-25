package me.cybulski.civ5pbemserver.user;

/**
 * @author Michał Cybulski
 */
public class TestUserAccountFactory {

    private UserAccountFactory userAccountFactory = new UserAccountFactory();

    public UserAccount createNewUserAccount(String email, String username) {
        return userAccountFactory.createUserAccount(email, username);
    }
}
