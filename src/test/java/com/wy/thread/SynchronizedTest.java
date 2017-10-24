package com.wy.thread;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wy-zyd on 17-10-11.
 */
@Slf4j
public class SynchronizedTest {
    static int i=0;
    static class SynchMathed{
        //对象级别锁
        public synchronized void testAdd1(){
            try {
                i++;
                log.info("Thread name:{},method name:{}",Thread.currentThread().getName(),"testAdd1");
                Thread.sleep(5000L);
                log.info("Thread name:{},i:{}",Thread.currentThread().getName(),i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        //对象级别锁
        public synchronized void testAdd2() {
            try {
                log.info("Thread name:{},method name:{}",Thread.currentThread().getName(),"testAdd2");
                Thread.sleep(6000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //类级别锁
        public static  synchronized   void testAdd3() {
            try {
                log.info("Thread name:{},method name:{}",Thread.currentThread().getName(),"testAdd3");
                Thread.sleep(7000L);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        public static  synchronized   void testAdd4() {
            try {
                log.info("Thread name:{},method name:{}",Thread.currentThread().getName(),"testAdd4");
                Thread.sleep(4000L);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    ExecutorService executorService = Executors.newFixedThreadPool(10);
    @Test
    public void testMethodLockObject() throws InterruptedException {
        //对象级别锁互斥,类级别锁互斥,类和对象级别锁可同时加
        //同步代码块的使用的是对象级别的锁,具有相同的
        SynchMathed synchMathed = new SynchMathed();
        executorService.submit(() ->{//可以重入
            synchMathed.testAdd1();
        } );
//        executorService.submit(() -> SynchMathed.testAdd3());
//
//        executorService.submit(() -> synchMathed.testAdd2());
//        executorService.submit(() -> SynchMathed.testAdd4());
        log.info("Thread name:{},i:{}",Thread.currentThread().getName(),i);
        Thread.sleep(3000L);
        log.info("Thread name:{},i:{}",Thread.currentThread().getName(),i);
        Thread.sleep(3000L);
        log.info("Thread name:{},method name:{}",Thread.currentThread().getName(),"main");
    }
}
