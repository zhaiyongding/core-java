package com.wy.thread;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;


/**
 * Created by wy-zyd on 17-10-10.
 */
@Slf4j
public class ThreadInterruptTest {


    @Test
    public void testThreadInterrupt() throws InterruptedException {
        Thread myThread = new Thread(new MyThread());
        myThread.start();
        //myThread.join();
        myThread.interrupt();
        log.info("myThread:{},Interrupt:{},isAlive;{}",myThread.getName(),myThread.isInterrupted(),myThread.isAlive());
        log.info("myThread:{},Interrupt:{},isAlive;{}",myThread.getName(),myThread.isInterrupted(),myThread.isAlive());

        log.info("myThread:{},Interrupt:{},isAlive;{}",Thread.currentThread().getName(),myThread.interrupted(),myThread.isAlive());
    }
    public  static class MyThread implements Runnable{

        @Override
        public void run() {
            for (int i = 0; i < 1000; i++) {
                if (i % 100 == 0) {
                    log.info("----i ={}", i);
                }
            }
        }
    }
}
