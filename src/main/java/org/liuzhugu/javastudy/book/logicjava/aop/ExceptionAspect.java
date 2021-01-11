package org.liuzhugu.javastudy.book.logicjava.aop;


import org.liuzhugu.javastudy.book.logicjava.annotation.ServiceB;

import java.lang.reflect.Method;
import java.util.Arrays;

//在spring中,不用如此写死，只需要遍历所有类,判断什么类使用了该注解即可
@Aspect({ServiceB.class})
public class ExceptionAspect {
    public static void exception(Object obj, Method method,Object[] args,
                                 Throwable e) {
        System.out.println("exception when calling: "
            + method.getName() + "," + Arrays.toString(args));
    }
}
