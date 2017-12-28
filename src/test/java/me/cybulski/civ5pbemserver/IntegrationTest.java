package me.cybulski.civ5pbemserver;

import me.cybulski.civ5pbemserver.game.SaveGameSynchronizer;
import me.cybulski.civ5pbemserver.game.SaveGameValidator;
import me.cybulski.civ5pbemserver.mail.MailService;
import me.cybulski.civ5pbemserver.saveparser.SaveGameParser;
import org.junit.After;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static me.cybulski.civ5pbemserver.config.Profiles.TEST;

/**
 * @author Michał Cybulski
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestEntityManager
@Transactional
@ActiveProfiles(TEST)
public abstract class IntegrationTest {

    @Autowired
    protected TestEntityManager testEntityManager;

    // it's very hard to test these two in integration tests and they break things up
    @MockBean
    protected SaveGameParser saveGameParser;
    @MockBean
    protected SaveGameValidator saveGameValidator;
    @MockBean
    protected SaveGameSynchronizer saveGameSynchronizer;

    @SpyBean
    protected MailService mailService;

    @After
    public void flushDb() {
        testEntityManager.flush();
    }
}
