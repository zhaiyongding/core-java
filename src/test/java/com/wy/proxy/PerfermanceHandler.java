package com.wy.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by zhaiyongding on 2016/8/11.
 */
//AOP横切模块
public class PerfermanceHandler implements InvocationHandler {
    private Object target;

    public PerfermanceHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        PerformanceMonitor.begin(target.getClass().getName()+"."+method.getName());
        //Object object = method.invoke(target, args);
        //如果是mybatis 解析参数，解析方法名，获取连接池，直接执行查询并组装结果集
        PerformanceMonitor.end();
        return null;
    }

}