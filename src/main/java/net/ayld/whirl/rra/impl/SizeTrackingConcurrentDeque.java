package net.ayld.whirl.rra.impl;

import java.util.concurrent.ConcurrentLinkedDeque;

/**
 *
 * Created by siliev on 14-6-25.
 */
public class SizeTrackingConcurrentDeque<T> extends ConcurrentLinkedDeque<T> {

    private int size;

    public SizeTrackingConcurrentDeque() {
        super();
    }

    // TODO: possibly override and throw UnsupportedOperationException on other state modifier methods

    @Override
    public void push(T o) {
        size++;
        super.push(o);
    }

    @Override
    public T pollLast() {
        T removed = super.pollLast();
        size--;
        return removed;
    }

    @Override
    public int size() {
        return this.size;
    }
}
