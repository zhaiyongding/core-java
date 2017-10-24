package com.wy.core;

import org.junit.Test;

/**
 * Created by wy-zyd on 17-9-19.
 */
public class EffectivelyFinal {
    @Test
    public void testFinalJava8() {
        //局部内部类和匿名内部类访问的局部变量必须由final修饰，java8开始，可以不加final修饰符，由系统默认添加

        //当程序没有尝试修改age时候,编译不会提示错误,如果尝试改变age,编译无法通过
        int age = 99;

        new A() {
            public void test() {
                System.out.println(age);

                //age++;
            }
        }.test();

//        age++;

    }

    interface A {
        void test();
    }
}
