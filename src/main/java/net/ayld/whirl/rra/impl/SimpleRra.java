package net.ayld.whirl.rra.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import net.ayld.whirl.model.Period;
import net.ayld.whirl.model.Resolution;
import net.ayld.whirl.rra.ConcurrencyPolicy;
import net.ayld.whirl.rra.RoundRobinArchive;

import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 * Created by siliev on 14-6-25.
 */
public class SimpleRra implements RoundRobinArchive{

    private static final Map<ConcurrencyPolicy, Boolean> POLICY_TO_REENTRANT_POLICY = new HashMap<ConcurrencyPolicy, Boolean>(){{
        put(ConcurrencyPolicy.FAIR, true);
        put(ConcurrencyPolicy.DEFAULT, false);
        put(ConcurrencyPolicy.NONE, false);
    }};

    private final Deque<AtomicLong> buckets = new SizeTrackingConcurrentDeque<>();
    private final Map<Period, AtomicLong> rollingSums = Maps.newConcurrentMap();

    private final Resolution resolution;

    private final boolean performLocking;
    private final Lock readLock;
    private final Lock writeLock;

    @SuppressWarnings("unused")
    private final RraSizeManager sizeManager;

    public SimpleRra(Resolution resolution, Period lifetime, ConcurrencyPolicy concurrencyPolicy) {
        this.resolution = resolution;
        this.performLocking = concurrencyPolicy != ConcurrencyPolicy.NONE;
        
        final Boolean reentrantPolicy = POLICY_TO_REENTRANT_POLICY.get(concurrencyPolicy);
        
        final ReadWriteLock lock = new ReentrantReadWriteLock(reentrantPolicy);
        this.readLock = lock.readLock();
        this.writeLock = lock.writeLock();

        initSums();

        final int life = lifetime.inSeconds();
        final int rate = resolution.shortestSupported().inSeconds();

        this.sizeManager = new RraSizeManager(buckets, rollingSums, rate, life);
    }

    private void initSums() {
        for (Period p : resolution.supported()) {
            rollingSums.put(p, new AtomicLong()); // no need for atomic longs when we're locking buckets
        }
    }

    @Override
    public void increment() {
        if (performLocking) {
            writeLock.lock();
        }
        try {
            buckets.peek().incrementAndGet();
            for (Period p : resolution.supported()) {
                rollingSums.get(p).incrementAndGet();
            }
        } finally {
            if (performLocking) {
                writeLock.unlock();
            }
        }
    }

    @Override
    public void decrement() {
        if (performLocking) {
            writeLock.lock();
        }
        try {
            buckets.peek().decrementAndGet();
            for (Period p : resolution.supported()) {
                rollingSums.get(p).decrementAndGet();
            }
        } finally {
            if (performLocking) {
                writeLock.unlock();
            }
        }
    }

    @Override
    public long getValue(Period period) {
        if (performLocking) {
            readLock.lock();
        }
        final long result;
        try {
            result = rollingSums.get(period).longValue();
        } finally {
            if (performLocking) {
                readLock.unlock();
            }
        }
        return result;
    }

    public List<AtomicLong> bucketSnapshot() {
        if (performLocking) {
            writeLock.lock();
        }
        final ImmutableList<AtomicLong> result;
        try {
            result = ImmutableList.copyOf(buckets);
        } finally {
            if (performLocking) {
                writeLock.unlock();
            }
        }
        return result;
    }
}
