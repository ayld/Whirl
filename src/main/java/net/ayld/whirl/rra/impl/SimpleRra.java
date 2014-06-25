package net.ayld.whirl.rra.impl;

import net.ayld.whirl.model.Period;
import net.ayld.whirl.rra.RoundRobinArchive;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * Created by siliev on 14-6-25.
 */
public class SimpleRra implements RoundRobinArchive{

    private long size;
    private final Deque<AtomicLong> store = new ConcurrentLinkedDeque<>();

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public SimpleRra() {

    }

    @Override
    public void increment() {

    }

    @Override
    public void decrement() {

    }

    @Override
    public void size() {

    }

    @Override
    public void getSum(Period period) {

    }
}
