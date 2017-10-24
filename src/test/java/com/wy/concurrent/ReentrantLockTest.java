package com.wy.concurrent;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * Created by wy-zyd on 17-10-24.
 */
@Slf4j
public class ReentrantLockTest {

    @Test
    public void testLock() throws InterruptedException, ExecutionException {
        AtomicInteger luckNum = new AtomicInteger(0);
        final ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(4);

        CountDownLatch countdown = new CountDownLatch(100);

        IntStream.range(0, 100).forEach(index -> {
            threadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    int level = luck(index);
                    if (level > 0) {
                        luckNum.incrementAndGet();
                    }
                    //log.info("luck level :{}",level);
                    countdown.countDown();
                }
            });
        });
        countdown.await();
        log.info("luck num:{},rate:{}", luckNum.get(), luckNum.get() / 100.0);
    }

    private final static ReentrantLock lock = new ReentrantLock();

    //相比较synchronized 灵活并多列尝试加锁,而且利用Condition 对不同情形阻塞和唤起
    private static int luck(int luck) {
        int level = RandomUtils.nextInt(2);
        if (level == 1) {//大奖
            if (lock.tryLock() ) {
                try {//如果没有竞争成功lock 如果有竞争不阻塞返回失败
                    //赢得竞争有机会中大奖
                    Thread.sleep(100L);
                    level = RandomUtils.nextInt(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }else {
                level = 0;
            }
        } else {
            level = 0;//降级小奖
        }


        return level;
    }
}
