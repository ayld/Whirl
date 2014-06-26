package net.ayld.whirl.rra;

/**
 * Concurrency policy for a RRA archive.
 * Policies differ in consistency vs. speed.
 *
 * Higher consistency means lower throughput due to extensive locking.
 *
 * @see {@link net.ayld.whirl.rra.RoundRobinArchive}
 *
 * Created by siliev on 14-6-26.
 */
public enum ConcurrencyPolicy {
    /**
     * Guarantees consistency somewhat faster than the FAIR policy.
     *
     * Uses ReentrantLocks default locking mechanisms to guarantee values get placed in correct buckets.
     * Can lead to thread "starvation" if a single thread is "unlucky" enough to not be able
     * to acquire a lock for a prolonged period.
     * */
    DEFAULT,

    /**
     * Guarantees consistency and slowest setting.
     *
     * Uses ReentrantLocks to guarantee that locks will be acquired
     * by the most "starved" thread (the one waiting the longest).
     *
     * You can use this to guarantee lack of starvation and no bucket jumping.
     * */
    FAIR,

    /**
     * Least consistent and fastest setting.
     * Performs no locking.
     *
     * Can lead to values "jumping" from the bucket they should be in to the next.
     * In case of prolonged thread "starvation" they can even jump several buckets
     * for higher resolution RRAs, being access by a large number of threads.
     *
     * This policy may also cause lost updates and negative bucket values.
     *
     * This is usable when you expect a low amount of threads to modify the RRA.
     * Or you don't need 100% consistency.
     * */
    NONE;
}
