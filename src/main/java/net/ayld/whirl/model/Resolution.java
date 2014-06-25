package net.ayld.whirl.model;

import com.google.common.collect.ImmutableSet;

import static net.ayld.whirl.model.Period.DAY;
import static net.ayld.whirl.model.Period.HALF_DAY;
import static net.ayld.whirl.model.Period.HOUR;
import static net.ayld.whirl.model.Period.MINUTE;
import static net.ayld.whirl.model.Period.SECOND;

/**
 *
 * Created by siliev on 14-6-25.
 */
public enum Resolution {
    PER_SECOND(SECOND, MINUTE, HOUR),
    PER_MINUTE(MINUTE, HOUR, HALF_DAY, DAY);

    private final Period[] supports;

    Resolution(Period... supports) {
        this.supports = supports;
    }

    // todo: optimize this not to create a set every time
    // add a cache or something
    public boolean supports(Period p) {
        return ImmutableSet.copyOf(supports).contains(p);
    }
}
