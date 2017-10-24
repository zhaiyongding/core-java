package com.wy.concurrent;


import com.huaban.analysis.jieba.JiebaSegmenter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.RandomUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 任务帮助类
 */
@Slf4j
public class TaskHelper {
    private static Long startTime = 0L;
    private static ArrayList<Long> qps = new ArrayList<>();
    private static AtomicLong count = new AtomicLong(0L);
    private static Long lastCount = 0L;
    private static Timer timer = new Timer("TaskHelper");
    private static DecimalFormat df=(DecimalFormat) NumberFormat.getInstance();

    //用于实时统计任务执行效率
    public static void runTaskCount() {
        count.addAndGet(1);
    }

    public static void startTask() {
        startTime=System.currentTimeMillis();
    }
    public static void taksReport() {
        Long endTime =System.currentTimeMillis();
        Double taskTime = (endTime - startTime)/1000.0;
        timer.cancel();
        log.info("\n执行时间:{}s,\n" +
                "任务数:{},\n" +
                "平均吞吐量:{}/s,\n" +
                "平均任务耗时:{}",taskTime,
                count.get(),df.format(count.get()/taskTime), df.format(taskTime.doubleValue()/count.get()) );
        StringBuffer report = new StringBuffer();

        for (int index =0; index< qps.size(); index++) {
            report.append(qps.get(index));
            if (index % 5==4) {
                report.append("\n");
            }else {
                report.append("\t");
            }
        }
        log.info("任务处理报告:\n" +report);
    }
    //定时刷新计算count增长效率
    public   static Set<String> filterChar = new HashSet<>();
    static {
        filterChar.add("，"); filterChar.add("“");
        filterChar.add("。");filterChar.add("-");
        filterChar.add("、");filterChar.add("各");
        filterChar.add("　");filterChar.add("、");
        filterChar.add("和");filterChar.add("-");
        filterChar.add("”");filterChar.add("　　");
        filterChar.add("是");filterChar.add("的");
        filterChar.add("为");filterChar.add("-");
        filterChar.add("：");filterChar.add("-");
        filterChar.add("而");filterChar.add("-");
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Long lastCountTemp = lastCount;
                lastCount = count.get();
                qps.add(lastCount - lastCountTemp);
                log.info("当前执行效率:{}/s,当前处理数量:{}", lastCount - lastCountTemp, lastCount);
            }
        }, 10, 1000);
    }
    //定时刷新计算count增长效率
    /**
    static {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(new Runnable() {
            @Override
            public void run() {
                Long lastCountTemp = lastCount;
                lastCount = count.get();
                qps.add(lastCount - lastCountTemp);
                log.info("当前执行效率:{}/s,当前处理数量:{}", lastCount - lastCountTemp, lastCount);
            }
        },1, TimeUnit.SECONDS);

    }
     **/

    //辅助工具
    public static int getTextLineNum(String filename){
        File text= new File(filename);
        long fileLength = text.length();
        LineNumberReader rf = null;
        try {
            rf = new LineNumberReader(new FileReader(text));
            if (rf != null) {
                int lines = 0;
                rf.skip(fileLength);
                lines = rf.getLineNumber();
                rf.close();
                return lines;
            }
        } catch (IOException e) {
            if (rf != null) {
                try {
                    rf.close();
                } catch (IOException ee) {
                }
            }
        }
        return 0;
    }

    /**
     * 倒序排列
     * @param map
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V extends Comparable<? super V>> LinkedHashMap<K, V> sortByValue7(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        LinkedHashMap<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * 正序排列
     * @param map
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V extends Comparable<? super V>> LinkedHashMap<K, V> sortByValue8(Map<K, V> map) {
        LinkedHashMap<K, V> result = new LinkedHashMap<>();
        Stream<Map.Entry<K, V>> st = map.entrySet().stream();

        st.sorted(Comparator.comparing(e -> e.getValue())).forEach(e -> result.put(e.getKey(), e.getValue()));
        return result;
    }

    public  static <K, V extends Comparable<? super V>> LinkedHashMap<K, V>  filterMap(LinkedHashMap<K, V> map,int num) {
        LinkedHashMap<K, V> result = new LinkedHashMap<>();
        AtomicInteger index = new AtomicInteger();
        map.forEach((k, v) ->{
            if(index.getAndIncrement()<num){
                result.put(k, v);
            }
        });
        return result;
    }

    public static void megerMap(ConcurrentHashMap<Long, Integer> productOrder, HashMap<Long, Integer> longIntegerHashMap) {

        for(Long key:longIntegerHashMap.keySet()) {
            megerMap4(productOrder, key, longIntegerHashMap.get(key));
        }
    }
    //任意jdk
    public static void megerMap1(ConcurrentHashMap<Long, Integer> productOrder, Long key,Integer v) {
        //类CAS的合并
        Integer oldValue = null;
        Integer newValue = null;
        do {
            oldValue = productOrder.get(key);
            newValue = oldValue == null ? v : v + oldValue;
        } while (!productOrder.replace(key, oldValue, newValue));
    }

    public static void megerMap2(ConcurrentHashMap<Long, Integer> productOrder, Long key,Integer val) {
        productOrder.compute(key, (k, v) -> v == null ? val : val + v);
    }

    public static void megerMap4(ConcurrentHashMap<Long, Integer> productOrder, Long key,Integer val) {
        productOrder.merge(key,val, Integer::sum);
    }
}
