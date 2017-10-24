package com.wy.proxy;

/**
 * Created by zhaiyongding on 2016/8/11.
 */
//线程安全的横切逻辑
public class PerformanceMonitor {
    private static ThreadLocal<MethodPerformance> tl= new ThreadLocal<MethodPerformance>();

    public static void begin(String method){
        System.out.println("begin monitor");
        MethodPerformance mp = new MethodPerformance(method);
        tl.set(mp);
    }

    public static void end(){
        System.out.println("end monitor");
        MethodPerformance mp = tl.get();
        mp.printPerformance();
    }
}