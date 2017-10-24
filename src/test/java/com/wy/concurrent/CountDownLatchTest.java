package com.wy.concurrent;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wy-zyd on 17-9-28.
 */
@Slf4j
public class CountDownLatchTest {
    @Test
    public void count2Thread() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(2);

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int a = 0; a < 1; a++) {

            executorService.execute(() -> {
                countDownLatch.countDown();
                log.info("count2Thread---name:{},count:{}", Thread.currentThread().getName(), countDownLatch.getCount());
            });

        }
        countDownLatch.await();
        log.info("count2Thread---name:{},count:{}", Thread.currentThread().getName(), countDownLatch.getCount());
    }
}
