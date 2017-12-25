package me.cybulski.civ5pbemserver.game;

import me.cybulski.civ5pbemserver.user.UserAccount;
import org.junit.Rule;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

/**
 * @author Michał Cybulski
 */
public abstract class BaseGameTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    @Mock
    UserAccount hostUserAccount;
}
