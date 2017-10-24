package com.wy.concurrent;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.concurrent.RecursiveTask;


/**
 * 不同数量线程求和
 * Created by wy-zyd on 17-10-16.
 */
@Slf4j
public class OrderTaksForkJoin extends RecursiveTask<LinkedHashMap<Long, Integer>> {
    public static final int threshold = 1;
    private int start;
    private int end;

    public OrderTaksForkJoin(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    protected LinkedHashMap<Long, Integer> compute() {
        LinkedHashMap<Long, Integer> linkedHashMap = new LinkedHashMap<Long, Integer>();

        //如果任务足够小就计算任务
        boolean canCompute = (end - start) == threshold;
        if (canCompute) {
            linkedHashMap = TaskTools.runUserOrderTask(start);
            TaskHelper.runTaskCount();//每个数字相当与一个任务
            log.info("task start:{}",start);
        } else {
            // 如果任务大于阈值，就分裂成两个子任务计算
            int middle = (start + end) / 2;

            OrderTaksForkJoin leftTask = new OrderTaksForkJoin(start, middle);
            OrderTaksForkJoin rightTask = new OrderTaksForkJoin(middle, end);

            // 执行子任务
            leftTask.fork();
            rightTask.fork();

            //等待任务执行结束合并其结果
            LinkedHashMap<Long, Integer> leftResult = leftTask.join();
            LinkedHashMap<Long, Integer> rightResult = rightTask.join();

            //合并子任务
            leftResult.putAll(rightResult);
            linkedHashMap=TaskHelper.filterMap(TaskHelper.sortByValue7(leftResult), 10);
        }

        return linkedHashMap;
    }


}
