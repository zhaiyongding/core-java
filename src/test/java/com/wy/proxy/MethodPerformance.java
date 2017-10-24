package com.wy.proxy;

/**
 * Created by zhaiyongding on 2016/8/11.
 */
//性能监控核心代码生成
public class MethodPerformance {
    private long begin;
    private long end;
    private String serviceMethod;

    public MethodPerformance(String serviceMethod) {
        this.serviceMethod = serviceMethod;
        this.begin = System.currentTimeMillis();
    };

    public void printPerformance(){
        this.end = System.currentTimeMillis();
        long elapse = this.end - this.begin;
        System.out.println(serviceMethod + " cost " + elapse +"ms");
    }
}
