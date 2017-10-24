package com.wy.concurrent;

import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.*;


/**
 * 不同数量线程求和
 * Created by wy-zyd on 17-10-16.
 */
@Slf4j
public class SumForkJoinTest {
    private  static  int taksnum = 1000000;
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
    @Test
    public void taskbyForkJoin() throws Exception  {
        //threadNum个线程来执行

        ForkJoinPool forkjoinPool = new ForkJoinPool(threadNum);//

        ForkJoinSumTask task = new ForkJoinSumTask(0, taksnum);

        Future<Long> result = forkjoinPool.submit(task);
        log.info("TaskbyForkJoin result:{}",result.get());

    }
    //RecursiveAction 没有返回结果 RecursiveTask 有返回结果
    class ForkJoinSumTask extends RecursiveTask<Long> {
        public static final int threshold = 100;
        private int start;
        private int end;

        public ForkJoinSumTask(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {
            Long sum = 0L;

            //如果任务足够小就计算任务
            boolean canCompute = (end - start) <= threshold;
            if (canCompute) {
                sum = singleTaskbySingle(start, end);
                TaskHelper.runTaskCount();//每个数字相当与一个任务
            } else {
                // 如果任务大于阈值，就分裂成两个子任务计算
                int middle = (start + end) / 2;

                ForkJoinSumTask leftTask = new ForkJoinSumTask(start, middle);
                ForkJoinSumTask rightTask = new ForkJoinSumTask(middle, end);

                // 执行子任务
                leftTask.fork();
                rightTask.fork();

                //等待任务执行结束合并其结果
                Long leftResult = leftTask.join();
                Long rightResult = rightTask.join();

                //合并子任务
                sum = leftResult + rightResult;

            }

            return sum;
        }
    }

}
