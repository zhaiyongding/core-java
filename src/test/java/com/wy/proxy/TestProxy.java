package com.wy.proxy;

import lombok.extern.slf4j.Slf4j;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.aop.BeforeAdvice;
import org.springframework.aop.framework.ProxyFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by zhaiyongding on 2016/8/11.
 */
@Slf4j
public class TestProxy {
    @Test
    public void jdkproxy() {
        //如果我们需要 新增一些功能到 ForumService里，实现有接口，我们可以代理这个接口
        ForumService target = new ForumServiceImpl();
        //将目标业务类与横切代码编织到一起
        PerfermanceHandler handler = new PerfermanceHandler(target);
        //创建代理实例
        ForumService proxy = (ForumService) Proxy.newProxyInstance(target.getClass().getClassLoader(),
                target.getClass().getInterfaces(), handler);
        proxy.removeForum(10);
        proxy.removeTopic(1012);
    }

    @Test
    public void jdkproxy2() {
        //如果我们需要 新增一些功能到 ForumService里，实现有接口，我们可以代理这个接口
        ForumService target = null;
        //将目标业务类与横切代码编织到一起
        PerfermanceHandler handler = new PerfermanceHandler(target);
        //创建代理实例
        ForumService proxy = (ForumService) Proxy.newProxyInstance(target.getClass().getClassLoader(),
                target.getClass().getInterfaces(), handler);
        proxy.removeForum(10);

    }

    @Test
    public void cglibproxy() {
        CglibProxy cglibProxy = new CglibProxy();
        ForumServiceImpl service = (ForumServiceImpl) cglibProxy.getProxy(ForumServiceImpl.class);
        service.removeForum(10);
        service.removeTopic(1012);
    }

    @Test
    public void springaop() {
        ForumServiceImpl target = new ForumServiceImpl();
        BeforeAdvice advice = new GreeteBeforeAdvice();
        GreeteAfterAdvice advice1 = new GreeteAfterAdvice();
        //Spring提供的代理工厂
        ProxyFactory pf = new ProxyFactory();//使用Cglib2AOPProx 即CGlib代理技术创建代理
        //设置代理目标
        pf.setTarget(target);
        //添加增强处理
        pf.addAdvice(advice);
        pf.addAdvice(advice1);

        //生成代理实例
        ForumService waiter = (ForumService) pf.getProxy();
        waiter.removeForum(1111);

    }

    static JedisPool jedisPool = null;

    @BeforeClass
    public static void init() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(10);
        jedisPoolConfig.setMaxIdle(10);
        jedisPoolConfig.setMaxWaitMillis(3000);
        jedisPool = new JedisPool(new JedisPoolConfig(), "120.26.106.94", 6379, 3000, "admin", 2);

    }

    @Test
    public void testJedisProxy() {

        for (int i = 0; i < 100; i++) {
            Jedis resource = jedisPool.getResource();
            JedisHandler jedisHandler = new JedisHandler(resource);
            JedisCommands proxy = (JedisCommands) Proxy.newProxyInstance(resource.getClass().getClassLoader(),
                    resource.getClass().getInterfaces(), jedisHandler);
            //log.info("before"+i+"::{}",jedisPool.getNumActive());
            proxy.decr("testjedis:" + i);
            //log.info("after"+i+"::{}",jedisPool.getNumActive());

        }
        System.out.println("");
    }

    //@Test
//    public  void  testJedisProxy2(){
//        RedisUtil redisUtil=new RedisUtil();
//        redisUtil.setJedisPool(jedisPool);
//        for(int i=0;i<100;i++){
//            log.info("before"+i+"::{}",jedisPool.getNumActive());
//            redisUtil.decr("testjedis:"+i);
//            log.info("after"+i+"::{}",jedisPool.getNumActive());
//        }
//
//    }
    public JedisCommands jdkRedisClientproxy(JedisPool jedisPool) {

        Jedis resource = jedisPool.getResource();
        JedisHandler jedisHandler = new JedisHandler(resource);
        Object proxy = Proxy.newProxyInstance(resource.getClass().getClassLoader(),
                resource.getClass().getInterfaces(), jedisHandler);
        return (JedisCommands) proxy;
    }
}

class JedisHandler implements InvocationHandler {
    private Jedis target;

    public JedisHandler(Jedis target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        Object object = null;
        //从连接池里获取资源,并设置
        if (!method.getName().equals("toString")) {
            try {
                object = method.invoke(target, args);
            } finally {
                target.close();
            }
        }
        return object;
    }

}
