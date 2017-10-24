package com.wy.thread;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;

import java.util.concurrent.*;

/**
 * Created by wy-zyd on 17-9-6.
 */
@Slf4j
public class ThreadLocalTest {

    public ThreadPoolExecutor getThreadPoolExecutor() {
        // 默认线程数，默认为Integer.1
        Integer corePool=2;
        //线程池最大的数量 Integer.MAX_VALUE
        Integer maximumPoolSize=6;
        //线程池维护线程所允许的空闲时间，默认为60s
        Long keepAliveTime = 5L;

        TimeUnit unit=TimeUnit.SECONDS;

        BlockingQueue<Runnable> synchronousQueue=new SynchronousQueue<Runnable>();//不存储元素阻塞队列
        BlockingQueue<Runnable> arrayBlockingQueue=new ArrayBlockingQueue<Runnable>(2);//有界阻塞队列
        BlockingQueue<Runnable> linkedBlockingDeque=new LinkedBlockingDeque<>(20);//有界阻塞队列
        BlockingQueue<Runnable> priorityBlockingQueue=new PriorityBlockingQueue<>(20);//无界阻塞队列
        ThreadFactory threadFactory= Executors.defaultThreadFactory();

        RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePool, maximumPoolSize,
                keepAliveTime, unit,
                arrayBlockingQueue,threadFactory,handler);

        return threadPoolExecutor;
    }
    // ①通过匿名内部类覆盖ThreadLocal的initialValue()方法，指定初始值
    private static ThreadLocal<Integer> seqNum = new ThreadLocal<Integer>() {
        public Integer initialValue() {
            return 0;
        }
    };

    // ②获取下一个序列值
    public int getNextNum() {
        seqNum.set(seqNum.get() + 1);
        return seqNum.get();
    }
    @Test
    public void testThreadLocal() {
        ThreadPoolExecutor poolExecutor = getThreadPoolExecutor();
        for(Integer i=1;i<20;i++){
            poolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    log.info("测试:::{} :{}",Thread.currentThread().getName()+":"+Thread.currentThread().getId(),getNextNum());
                    //seqNum.remove();
                }
            });
            if(i%10==0){
                try {
                    Thread.sleep(20000L);
                } catch (InterruptedException e) {
                }
            }

        }
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
        }
    }

    @Test
    public void addTnum() {
        ThreadPoolExecutor poolExecutor = getThreadPoolExecutor();
        for(Integer i=0;i<20;i++){
            poolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    log.info("测试:::{},{} ",Thread.currentThread().getName());
                }
            });
        }
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
        }
    }
}

