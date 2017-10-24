package com.wy.concurrent;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;

import java.util.concurrent.*;

@Slf4j
public class ThreadPoolTest {

    public ThreadPoolExecutor getThreadPoolExecutor() {
        // 默认线程数，默认为Integer.1
        Integer corePool = 5;
        //线程池最大的数量 Integer.MAX_VALUE
        Integer maximumPoolSize = 8;
        //线程池维护线程所允许的空闲时间，默认为60s
        Long keepAliveTime = 60L;

        TimeUnit unit = TimeUnit.SECONDS;

        BlockingQueue<Runnable> synchronousQueue = new SynchronousQueue<Runnable>();//不存储元素阻塞队列
        BlockingQueue<Runnable> arrayBlockingQueue = new ArrayBlockingQueue<Runnable>(20);//有界阻塞队列
        BlockingQueue<Runnable> linkedBlockingDeque = new LinkedBlockingDeque<>(20);//有界阻塞队列
        BlockingQueue<Runnable> priorityBlockingQueue = new PriorityBlockingQueue<>(20);//无界阻塞队列

        ThreadFactory threadFactory = Executors.defaultThreadFactory();

        RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePool, maximumPoolSize,
                keepAliveTime, unit,
                synchronousQueue, threadFactory, handler);

        return threadPoolExecutor;
    }


    @Test
    public void threadPoolExecutor() throws Exception {

        final ThreadPoolExecutor threadPoolExecutor = getThreadPoolExecutor();
        for (Integer i = 0; i < 100; i++) {
            final Integer finalI = i;
            threadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    log.info("start {},{},{}", finalI, threadPoolExecutor.getActiveCount(), threadPoolExecutor.getQueue().size());
                    try {
                        Thread.sleep(5000L + RandomUtils.nextInt(5000));
                    } catch (InterruptedException e) {
                    }
                    //log.info("end  {},{},{}", finalI, threadPoolExecutor.getActiveCount(), threadPoolExecutor.getQueue().size());
                }
            });
            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
            }
        }

    }




}

