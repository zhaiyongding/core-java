package com.wy.concurrent;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Created by wy-zyd on 17-10-9.
 */
@Slf4j

public class SemaphoreTest {
    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Test
    public void testBaseSemapore() throws InterruptedException {
        Semaphore semaphore = new Semaphore(2);

        for (int i = 0; i < 10; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.currentThread().join();
                        semaphore.acquire();
                        Thread.sleep(1000l);
                        log.info("线程:{}", Thread.currentThread().getName());

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        semaphore.release();
                    }

                }
            });

        }


        log.info("test over");
    }
}
