package com.wy.concurrent;


import com.huaban.analysis.jieba.JiebaSegmenter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.RandomUtils;

import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * 任务累
 * Created by wy-zyd on 17-10-16.
 */
@Slf4j
public class TaskTools {

    //模拟任务,返回随机数值
    public static Long runTask(Long taskId) {
        try {
            Long sl = RandomUtils.nextInt(2) + 0L;
            Thread.sleep(sl);//10毫秒
            return sl;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 0L;
    }


    public static Map runJiebaTask(String fileName, Integer lineNumber, Integer endlineNumber) {
        JiebaSegmenter segmenter = new JiebaSegmenter();
        List<String> wordlist = new ArrayList<>();
        Map<String, Long> wordNum = new HashMap<>();
        LineNumberReader lineNumberReader = null;
        try {
            lineNumberReader = new LineNumberReader(Files.newBufferedReader(Paths.get(fileName), Charset.defaultCharset()));

            lineNumberReader.setLineNumber(lineNumber);

            while (lineNumberReader.getLineNumber() < endlineNumber) {
                wordlist.addAll(segmenter.sentenceProcess(lineNumberReader.readLine()));
            }
            wordNum = wordlist.stream().filter(word -> !TaskHelper.filterChar.contains(word)).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
            return wordNum;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                lineNumberReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return wordNum;
    }

    //用户订单 userId范围 倒序排列
    public static LinkedHashMap<Long, Integer> runUserOrderTask(int i) {
        LinkedHashMap<Long, Integer> mockLong = new LinkedHashMap<>();

        //按照表生成用户id和随机定义用户订单数目

        LongStream.range(0, 100).forEach(key -> mockLong.put(key * 100 + i, RandomUtils.nextInt(10000)));
        try {
            Thread.sleep(RandomUtils.nextInt(100) + 0L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return TaskHelper.filterMap(TaskHelper.sortByValue7(mockLong), 10);//排序并截取前10名
    }
    //产品 - 订单量  倒序排列
    public static HashMap<Long, Integer> runUserOrderTask4Product(int i) {
        HashMap<Long, Integer> mockLong = new HashMap<>();

        //按照表生成用户id和随机定义用户订单数目

        LongStream.range(0, 100).forEach(key -> mockLong.put(key, RandomUtils.nextInt(100)));
        try {
            Thread.sleep(RandomUtils.nextInt(100) + 0L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return mockLong;//全部产品
    }
}
