package com.wy.thread;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SynchronizedLockTest {
    private Integer lock=0;
    private Integer lock2=0;
    public void method1(){
        log.info("Method 1 start");
        try {
            synchronized (lock) {
                log.info("Method 1 execute");
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("Method 1 end");
    }

    public void method2(){
        log.info("Method 2 start");
        try {
            synchronized (lock2) {
                log.info("Method 2 execute");
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("Method 2 end");
    }

    public static void main(String[] args) {
        final SynchronizedLockTest test = new SynchronizedLockTest();

        new Thread(new Runnable() {
            @Override
            public void run() {
                test.method1();
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                test.method2();
            }
        }).start();
    }
}