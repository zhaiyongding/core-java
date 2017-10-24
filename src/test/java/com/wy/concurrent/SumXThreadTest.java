package com.wy.concurrent;

import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;


/**
 * 不同数量线程求和
 * Created by wy-zyd on 17-10-16.
 */
@Slf4j
public class SumXThreadTest {
    private  static  int taksnum = 10000000;
    private static int threadNum = 10;


    @Before
    public void taksBefore(){
        TaskHelper.startTask();
    }
    @After
    public void taksAfter(){
        TaskHelper.taksReport();
    }


    /**
     * 单任务
     */
    public static  long singleTaskbySingle(int start,int end) {
        log.info("start:{},end:{}",start,end);
        Long total = 0L;
        for (long i = start; i < end; i++) {
            TaskHelper.runTaskCount();//每个数字相当与一个任务
            total += i;
        }
        return total;
    }
    //单线程求和
    @Test
    public void taskbySingleThread() {

        Long total =singleTaskbySingle(0,taksnum);

        log.info("SumbySingleThread result:{}", total);
    }

    //多线程求和
    @Test
    public void taskbyMultiThread() throws Exception  {
        AtomicLong total = new AtomicLong(0);
        //平均分为threadNum份,交给threadNum线程做
        //用户等待各个线程完成main线程输出结果和报告
        CountDownLatch countdown = new CountDownLatch(threadNum);

        int taskNum4ST = taksnum / threadNum;
        for (int tid = 0; tid <threadNum; tid++) {
            int finalTid = tid;
            new Thread (new Runnable() {
                @Override
                public void run(){
                    Long innertotal= singleTaskbySingle(finalTid*taskNum4ST,finalTid*taskNum4ST+taskNum4ST);
                    total.addAndGet(innertotal);
                    countdown.countDown();
                }
            }).start();
        }
        countdown.await();
        log.info("TaskbyMultiThread result:{}", total);
    }
    //多线程求和
    @Test
    public void taskbyMultiThreadPool() throws Exception  {
        final ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(threadNum);
        Long total = new Long(0);
        //平均分为threadNum份,交给threadNum线程做
        //用户等待各个线程完成main线程输出结果和报告
        CountDownLatch countdown = new CountDownLatch(threadNum);
        int taskNum4ST = taksnum / threadNum;
        List<Future<Long>> flist = new ArrayList();
        for (int tid = 0; tid <threadNum; tid++) {
            int finalTid = tid;
            Future<Long> submit = threadPoolExecutor.submit(new Callable<Long>() {
                @Override
                public Long call() {
                    Long innertotal = singleTaskbySingle(finalTid * taskNum4ST, finalTid * taskNum4ST + taskNum4ST);
                    countdown.countDown();
                    return innertotal;
                }
            });
            flist.add(submit);
        }
        countdown.await();
        for (Future<Long> future:flist) {
            total += future.get();
        }
        log.info("TaskbyMultiThread result:{}", total);
    }

}
