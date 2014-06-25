package net.ayld.whirl.rra.impl;

import com.google.common.collect.Maps;
import net.ayld.whirl.model.Period;
import net.ayld.whirl.model.Resolution;
import net.ayld.whirl.rra.RoundRobinArchive;

import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * Created by siliev on 14-6-25.
 */
public class SimpleRra implements RoundRobinArchive{

    private long size;
    private final Deque<AtomicLong> store = new SizeTrackingConcurrentDeque<>();
    private final Map<Period, AtomicLong> rollingSums = Maps.newConcurrentMap();

    private final Resolution resolution;
    private final Period longest;

    private final RraSizeManager sizeManager;

    public SimpleRra(Resolution resolution, Period longest) {
        this.resolution = resolution;
        this.longest = longest;

        initSums();

        final int life = resolution.longestSupported().inSeconds();
        final int rate = resolution.shortestSupported().inSeconds();

        this.sizeManager = new RraSizeManager(store, rollingSums, rate, life);
    }

    private void initSums() {
        for (Period p : resolution.supported()) {
            rollingSums.put(p, new AtomicLong());
        }
    }

    @Override
    public void increment() {
        // TODO bucket and sum change should be atomic
        store.peek().incrementAndGet();
        for (Period p : resolution.supported()) {
            rollingSums.get(p).incrementAndGet();
        }
    }

    @Override
    public void decrement() {
        // TODO bucket and sum change should be atomic
        store.peek().decrementAndGet();
        for (Period p : resolution.supported()) {
            rollingSums.get(p).decrementAndGet();
        }
    }

    @Override
    public void getSum(Period period) {
        // TODO
        throw new UnsupportedOperationException();
    }
}
