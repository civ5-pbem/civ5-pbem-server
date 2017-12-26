package me.cybulski.civ5pbemserver.util;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * @author Michał Cybulski
 */
public class TimestampStringMatcher extends BaseMatcher<String> {

    private static final String DATE_PATTERN = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}Z";

    @Override
    public boolean matches(Object item) {
        if (item != null && String.class.isAssignableFrom(item.getClass())) {
            String dateString = (String) item;
            return dateString.matches(DATE_PATTERN);
        }

        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Should match: " + DATE_PATTERN);
    }
}
