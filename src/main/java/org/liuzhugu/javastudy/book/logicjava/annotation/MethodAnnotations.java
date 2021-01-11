package org.liuzhugu.javastudy.book.logicjava.annotation;

import java.lang.annotation.*;
import java.lang.reflect.Method;

public class MethodAnnotations {

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    static @interface QueryParam {
        String value();
    }

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    static @interface DefaultValue {
        String value() default "";
    }

    public void hello(@QueryParam("action") String action,
                      @QueryParam("sort") @DefaultValue("asc") String sort) {
        //...省略
    }

    public static void main(String[] args) throws Exception{
        //处理注解的代码
        Class<?> cls = MethodAnnotations.class;
        //获取方法
        Method method = cls.getMethod("hello",new Class[]{String.class,String.class});
        //获取方法的注解
        Annotation[][] annts = method.getParameterAnnotations();
        //处理注解
        for (int i = 0;i < annts.length;i ++) {
            Annotation[] anntArr = annts[i];
            for (Annotation annt : anntArr) {
                System.out.println("annotation for paramter " + (i + 1));
                if (annt instanceof QueryParam) {
                    QueryParam queryParam = (QueryParam)annt;
                    System.out.println(queryParam.annotationType().getSimpleName() + ":" + queryParam.value());
                }else if(annt instanceof DefaultValue) {
                    DefaultValue defaultValue = (DefaultValue)annt;
                    System.out.println(defaultValue.annotationType().getSimpleName() + ":" + defaultValue.value());
                }
            }
        }
    }
}
