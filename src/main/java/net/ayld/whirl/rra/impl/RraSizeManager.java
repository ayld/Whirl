package net.ayld.whirl.rra.impl;

import net.ayld.whirl.model.Period;

import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * Created by siliev on 14-6-25.
 */
public class RraSizeManager {

    // maybe this should be settable ?
    private final static int START_DELAY = 0;

    private long runs = 0;

    private final int rateSeconds;
    private final int lifeSeconds;

    private final Deque<AtomicLong> store;
    private final Map<Period, AtomicLong> rollingSums;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public RraSizeManager(Deque<AtomicLong> store, Map<Period, AtomicLong> rollingSums, int rateSeconds, int lifeSeconds) {
        this.store = store;
        this.rollingSums = rollingSums;
        this.rateSeconds = rateSeconds;
        this.lifeSeconds = lifeSeconds;
        start();
    }

    private void start() {
        final int maxBuckets = lifeSeconds / rateSeconds;
        final Period rate = Period.fromSeconds(rateSeconds);
        scheduler.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {

                // TODO think of order of operations here to maximize consistency

                addNew();

                final boolean hasReachedMaxSize = store.size() >= maxBuckets;
                if (hasReachedMaxSize) {
                    removeLast();
                }

                for (Period p : rollingSums.keySet()) {

                    final boolean resetSums = runs % p.in(rate) == 0;
                    if (resetSums) {
                        rollingSums.put(p, new AtomicLong());
                    }
                }

                runs++;
            }
        }, START_DELAY, rateSeconds, TimeUnit.SECONDS);
    }

    private void addNew() {
        store.push(new AtomicLong(0));
    }

    private AtomicLong removeLast() {
        return store.pollLast();
    }
}
