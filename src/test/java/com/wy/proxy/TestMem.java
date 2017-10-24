package com.wy.proxy;

import org.junit.Test;

/**
 * Created by wy-zyd on 17-5-31.
 */
public class TestMem {
/*
-Xms20m:表示堆的最小值为20M
-Xms20m:表示堆的最大值同样设置为20M,
-Xmn10M:表示新生代分配10M
-XX:SurvivorRatio=8:表示新生代的eden区:from区：to区:8:1:1
-XX:+PrintGCDetails 打印GC日志到控制台
-XX:PretenureSizeThreshold=3145728 	对象超过多大是直接在旧生代分配 3m
-XX:MaxTenuringThreshold =15
*/

    private static final int _1MB = 1024 * 1024;
    //  jdk 1.7

    // 直接测试大于阀值
    //PSYoungGen eden 如果足够不论是jdk7,8都直接分配大内存到PSYoungGen新生代,PretenureSizeThreshold没有关系
    @Test
    public void test78() {
        byte[] allocation1, allocation2, allocation3, allocation4;
        allocation1 = new byte[_1MB * 4];
        allocation2 = new byte[_1MB * 4];
        allocation4 = new byte[_1MB * 4];
        allocation3 = new byte[_1MB * 4];
        //allocation2 = new byte[_1MB * 12];
        Object wait=allocation1.length;
        Object wait2=allocation2.length;
        Object wait3=allocation4.length;
        Object wait4=allocation3.length;


    }
}
