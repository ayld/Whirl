package net.ayld.whirl.rra;

import net.ayld.whirl.model.Period;

/**
 *
 * Created by siliev on 14-6-25.
 */
public interface RoundRobinArchive {

    void increment();
    void decrement();

    long getValue(Period period);
}
