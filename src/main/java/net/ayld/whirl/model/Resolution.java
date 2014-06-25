package net.ayld.whirl.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

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

    private final List<Period> supports;

    Resolution(Period... supports) {
        this.supports = Lists.newArrayList(supports);
        Collections.sort(this.supports);
    }

    // todo: optimize this not to create a set every time
    // add a cache or something
    public boolean supports(Period p) {
        return ImmutableSet.copyOf(supports).contains(p);
    }

    public List<Period> supported() {
        return ImmutableList.copyOf(supports);
    }

    public Period longestSupported() {
        return supports.get(supports.size() - 1);
    }

    public Period shortestSupported() {
        return supports.get(0);
    }
}
