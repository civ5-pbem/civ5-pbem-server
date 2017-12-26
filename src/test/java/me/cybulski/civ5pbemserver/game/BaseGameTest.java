package me.cybulski.civ5pbemserver.game;

import me.cybulski.civ5pbemserver.user.UserAccount;
import org.junit.Before;
import org.junit.Rule;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;
import org.mockito.stubbing.Answer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @author Michał Cybulski
 */
public abstract class BaseGameTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    @Mock
    UserAccount hostUserAccount;
    @Mock
    GameRepository gameRepository;

    @Before
    public void setUpBase() {
        when(gameRepository.save(any())).thenAnswer((Answer<Game>) invocation -> invocation.getArgument(0));
    }
}
