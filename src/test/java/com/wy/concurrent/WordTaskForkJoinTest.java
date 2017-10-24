package com.wy.concurrent;

import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;


/**
 * 词频统计ForkJoin
 * Created by wy-zyd on 17-10-16.
 */
@Slf4j
public class WordTaskForkJoinTest {
    private static int threadNum = 10;

    @Before
    public void taksBefore() {
        TaskHelper.startTask();
    }

    @After
    public void taksAfter() {
        TaskHelper.taksReport();
    }


    private static String fileName = System.getProperty("user.dir") + "/target/classes/19da.txt";

    //private static String fileName = System.getProperty("user.dir")+"/target/classes/19da.txt";
    @Test
    public void taskbyForkJoin() throws Exception {
        //平均分为threadNum份,交给threadNum线程做
        int lineNumber = TaskHelper.getTextLineNum(fileName);

        ForkJoinPool forkjoinPool = new ForkJoinPool(threadNum);

        ForkJoinTask task = new ForkJoinTask(0, lineNumber);

        Future<Map> result = forkjoinPool.submit(task);
        log.info("TaskbyMultiThread result:");
        TaskHelper.sortByValue7(result.get()).forEach((k, v) -> System.out.println(k + ":" + v));

    }

    class ForkJoinTask extends RecursiveTask<Map> {
        public static final int threshold = 50;
        private int start;
        private int end;

        public ForkJoinTask(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        protected Map<String, Long> compute() {
            Map<String, Long> combinMap = new ConcurrentHashMap<>();

            //如果任务足够小就计算任务
            boolean canCompute = (end - start) <= threshold;
            if (canCompute) {
                combinMap = TaskTools.runJiebaTask(fileName, start, end);
            } else {
                // 如果任务大于阈值，就分裂成两个子任务计算
                int middle = (start + end) / 2;

                ForkJoinTask leftTask = new ForkJoinTask(start, middle);
                ForkJoinTask rightTask = new ForkJoinTask(middle, end);

                // 执行子任务
                leftTask.fork();
                rightTask.fork();

                //等待任务执行结束合并其结果
                Map<String, Long> leftResult = leftTask.join();
                Map<String, Long> rightResult = rightTask.join();

                //合并子任务

                combinMap = leftResult;
                for (String key : rightResult.keySet()) {
                    if (combinMap.containsKey(key)) {
                        combinMap.put(key, combinMap.get(key) + rightResult.get(key));
                    } else {
                        combinMap.put(key, rightResult.get(key));
                    }
                }
            }

            return combinMap;
        }
    }

}
