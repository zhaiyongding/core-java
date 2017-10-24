package com.wy.other;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by wy-zyd on 17-9-15.
 * //批量处理文件
 */
@Slf4j
public class BatchProcessFileTest {
    private static Semaphore semaphore = new Semaphore(100);
    LinkedBlockingDeque<String> blockingDeque = new LinkedBlockingDeque(100);
    //CocurrentLinkedQueue 优化

    ExecutorService executorService = Executors.newFixedThreadPool(120);
    int p = 0;
    static  String[] filenames = {"/home/wy-zyd/huiwen/h1.txt", "/home/wy-zyd/huiwen/h2.txt"};
    //
    private static AtomicLong count = new AtomicLong(0);

    @Test

    public void processFils() {

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                pickupFileFromQueue();
            }
        });

        addFileToQueue();
        log.info("addFileToQueue--{}", "添加完成");

        while (true) {
            if (p == filenames.length && blockingDeque.size() == 0 && semaphore.availablePermits() == 100 ) {//添加获取且处理完成
                log.info("end--{},count:{}", "处理完成", count.get());
                break;
            }
        }
    }

    public void addFileToQueue() {
        while (p < filenames.length) {
            if (blockingDeque.size() < 100) {
                blockingDeque.add(filenames[p]);
                p++;
            }
        }

    }

    public void pickupFileFromQueue() {
        while (true) {
            String filePath = blockingDeque.poll();
            if (filePath != null) {
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {

                        semaphore.tryAcquire();
                        //处理文件
                        try {
                            BufferedReader br = Files.newBufferedReader(Paths.get(filePath),Charset.defaultCharset());
                            //parallel 引入流和并行处理
                            long localCount = br.lines().parallel().filter(str -> str.equals(new StringBuffer(str).reverse().toString())).count();
                            long nowcount=count.addAndGet(localCount);
                            log.info("file:{},localCount:{},nowcount:{},semaphore:{}", filePath,localCount,nowcount,semaphore.availablePermits());

                            br.lines().parallel().count();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        semaphore.release();

                    }
                });
            } else {
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            if (p == filenames.length && blockingDeque.size() == 0) {//添加且处理完成
                log.info("end---{}", "添加获取完成");
                break;
            }
        }

    }

    @Test

    public void forkjoinProcess() {

        CountFileText total = new CountFileText(0, filenames.length);
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        ForkJoinTask<Long> submit = forkJoinPool.submit(total);
        try {
            System.out.println(submit.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    static class CountFileText extends RecursiveTask<Long> {
        Integer start;
        Integer end;
        public CountFileText(Integer start, Integer end) {
            this.start = start;
            this.end = end;
        }
        @Override
        protected Long compute() {
            Long count = 0L;
            if(end-start<=1){
                count=processStrEnd(start, end);
            }else{
                int mid = (start + end) / 2;
                CountFileText left = new CountFileText(start, mid);
                CountFileText right = new CountFileText(mid, end);
                left.fork();
                right.fork();
                count = left.join() + right.join();
            }
            return count;
        }
    }
    public static Long processStrEnd(Integer start, Integer end) {
        Long locCount10 = 0L;
        for(Integer s=start;s<end;s++) {
            String filePath = filenames[s];
            try {
                BufferedReader br = Files.newBufferedReader(Paths.get(filePath), Charset.defaultCharset());
                //parallel 引入流和并行处理
                //long localCount = br.lines().parallel().filter(str -> str.equals(new StringBuffer(str).reverse().toString())).count();
                //locCount10 += localCount;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return locCount10;
    }

}
