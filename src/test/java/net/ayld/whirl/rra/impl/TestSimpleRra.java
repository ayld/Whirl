
package net.ayld.whirl.rra.impl;

import com.google.common.collect.Lists;
import junit.framework.Assert;
import net.ayld.whirl.model.Period;
import net.ayld.whirl.model.Resolution;
import net.ayld.whirl.rra.ConcurrencyPolicy;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * Created by siliev on 14-6-26.
 */
public class TestSimpleRra {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    private SimpleRra rra;

    @Test
    public void correctlyReportsValues() throws Exception {
        rra = new SimpleRra(Resolution.PER_SECOND, Period.MINUTE, ConcurrencyPolicy.FAIR);

        int delay = 0;
        int rateMillis = 333;
        int threads = 4;

        final List<ScheduledFuture> futures = Lists.newArrayListWithExpectedSize(threads);
        for (int i = 0; i < threads; i++) {
            futures.add(scheduler.scheduleAtFixedRate(new Runnable() {

                @Override
                public void run() {

                    rra.increment();
                    rra.increment();
                    rra.decrement();
                }
            }, delay, rateMillis, TimeUnit.MILLISECONDS));
        }

        Thread.sleep(5000);

        for (ScheduledFuture f : futures) {
            f.cancel(true);
        }

        final List<AtomicLong> buckets = rra.bucketSnapshot();

        Assert.assertNotNull(buckets);
        Assert.assertTrue(buckets.size() >= 5);

        for (AtomicLong bucket : buckets) {
            System.out.println(bucket.longValue());

            Assert.assertNotNull(bucket);
        }
    }
}
