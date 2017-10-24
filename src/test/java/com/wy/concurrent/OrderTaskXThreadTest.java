package com.wy.concurrent;


import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.*;


/**
 * 用户销量排行榜
 * taskbySingleThread 单线程版本
 * taskbyMultiThread 多线程版本
 * taskbyThreadPoll 线程池banb
 * taskbyForkJoin fork/join版本
 */
@Slf4j
public class OrderTaskXThreadTest {
    //线程数
    private static int threadNum = 10;
    //任务数
    private static int taskNum = 100;

    @Before
    public void taksBefore() {
        TaskHelper.startTask();
    }

    @After
    public void taksAfter() {
        TaskHelper.taksReport();
    }

    /**
     * 单线程循环完成任务
     */
    @Test
    public void taskbySingleThread() {
        LinkedHashMap<Long, Integer> userOrder = new LinkedHashMap<>();
        for (int i = 0; i < taskNum; i++) {
            userOrder.putAll(TaskTools.runUserOrderTask(i));
            userOrder = TaskHelper.filterMap(TaskHelper.sortByValue7(userOrder), 10);//重新排序并截取前10
            TaskHelper.runTaskCount();
        }
        userOrder.forEach((k, v) -> System.out.println(k + ":" + v));
    }

    //由于有100个任务 同时要控制10个任务的并发度,我们用信号量来控制
    @Test
    public void taskbyMultiThread() throws InterruptedException {
        LinkedHashMap<Long, Integer> userOrder = new LinkedHashMap<>();//key 没有冲突不用担心线程安全问题

        CountDownLatch countdown = new CountDownLatch(taskNum);//计数器所有任务执行完,执行主线程
        Semaphore semaphore = new Semaphore(threadNum);
        for (int tid = 0; tid < taskNum; tid++) {
            int finalTid = tid;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //log.info("task start:{}",finalTid);
                        semaphore.acquire();//只有获取到信号量才会开始执行任务,避免瞬间开启过任务
                        log.info("semaphore acquire:{}", finalTid);
                        userOrder.putAll(TaskTools.runUserOrderTask(finalTid));
                        //FIXME 线程安全的put
                    } catch (Exception e) {

                    } finally {
                        countdown.countDown();
                        semaphore.release();
                        TaskHelper.runTaskCount();
                    }
                }
            }).start();
        }
        countdown.await();
        TaskHelper.filterMap(TaskHelper.sortByValue7(userOrder), 10).forEach((k, v) -> System.out.println(k + ":" + v));

    }

    @Test
    public void taskbyThreadPool() throws InterruptedException, ExecutionException {
        LinkedHashMap<Long, Integer> userOrder = new LinkedHashMap<>();//key 没有冲突不用担心线程安全问题
        //前10个任务新建线程,后面90个任务会放入阻塞队列中,不会新建线程
        final ExecutorService threadPoolExecutor = new ThreadPoolExecutor(
                threadNum, threadNum,
                10, TimeUnit.SECONDS,
                new ArrayBlockingQueue(taskNum), Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());
//        final ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(10);

        CountDownLatch countdown = new CountDownLatch(threadNum);

        List<Future<LinkedHashMap<Long, Integer>>> flist = new ArrayList();
        for (int tid = 0; tid < taskNum; tid++) {
            int finalTid = tid;
            Future<LinkedHashMap<Long, Integer>> submit = threadPoolExecutor.submit(new Callable<LinkedHashMap<Long, Integer>>() {
                @Override
                public LinkedHashMap<Long, Integer> call() {
                    log.info("task start:{}", finalTid);
                    LinkedHashMap<Long, Integer> result = TaskTools.runUserOrderTask(finalTid);
                    countdown.countDown();
                    TaskHelper.runTaskCount();
                    return result;
                }
            });
            flist.add(submit);
        }
        countdown.await();

        for (Future<LinkedHashMap<Long, Integer>> future : flist) {
            userOrder.putAll(future.get());
        }
        TaskHelper.filterMap(TaskHelper.sortByValue7(userOrder), 10).forEach((k, v) -> System.out.println(k + ":" + v));

    }

    @Test
    public void taskbyForkJoin() throws ExecutionException, InterruptedException {
        ForkJoinPool forkjoinPool = new ForkJoinPool(threadNum);

        OrderTaksForkJoin task = new OrderTaksForkJoin(0, taskNum);

        Future<LinkedHashMap<Long, Integer>> result = forkjoinPool.submit(task);

        result.get().forEach((k, v) -> System.out.println(k + ":" + v));
    }


    //线程安全的产品订单销量排行榜
    @Test
    public void taskbyMultiThread4Product() throws InterruptedException {
        ConcurrentHashMap<Long, Integer> productOrder = new ConcurrentHashMap<>();//key 没有冲突不用担心线程安全问题

        CountDownLatch countdown = new CountDownLatch(taskNum);//计数器所有任务执行完,执行主线程
        Semaphore semaphore = new Semaphore(threadNum);
        for (int tid = 0; tid < taskNum; tid++) {
            int finalTid = tid;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //log.info("task start:{}",finalTid);
                        semaphore.acquire();//只有获取到信号量才会开始执行任务,避免瞬间开启过任务
                        log.info("semaphore acquire:{}", finalTid);
                        TaskHelper.megerMap(productOrder, TaskTools.runUserOrderTask4Product(finalTid));
                    } catch (Exception e) {

                    } finally {
                        countdown.countDown();
                        semaphore.release();
                        TaskHelper.runTaskCount();
                    }
                }
            }).start();
        }
        countdown.await();
        TaskHelper.filterMap(TaskHelper.sortByValue7(productOrder), 10).forEach((k, v) -> System.out.println(k + ":" + v));

    }
}

