package com.wy.concurrent;

import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.stream.LongStream;


/**
 * java 8 流式求和
 * Created by wy-zyd on 17-10-16.
 */
@Slf4j
public class SumStreamTest {
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

    //java 8 单线程求和
    @Test
    public void taskbyStream() throws Exception  {

        long sum = LongStream.range(1, taksnum).reduce(0L, Long::sum);
        log.info("SumbySingleThread result:{}", sum);

    }
    //java 8 并行求和
    @Test
    public void taskbyStreamParallel() throws Exception  {

        long sum = LongStream.range(1, taksnum).parallel().reduce(0L, Long::sum);
        log.info("SumbySingleThread result:{}", sum);

    }


}
