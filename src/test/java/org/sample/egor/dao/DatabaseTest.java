package org.sample.egor.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseTest {

    @Test
    void lock() throws InterruptedException {
        AtomicBoolean test = new AtomicBoolean(false);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch finish = new CountDownLatch(2);
        Runnable t = () -> {
            try {
                Database.lock("key", () -> test.set(true));
                start.countDown();
                finish.countDown();
            } catch (InterruptedException e) {
                Assertions.fail(e.getMessage());
            }
        };

        Runnable t2 = () -> {
            try {
                start.await();
                Database.lock("key", () -> assertTrue(test.get()));
            } catch (InterruptedException e) {
                Assertions.fail(e.getMessage());
            } finally {
                finish.countDown();
            }
        };
        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
        forkJoinPool.execute(t);
        forkJoinPool.execute(t2);
        finish.await(5, TimeUnit.SECONDS);

    }
}