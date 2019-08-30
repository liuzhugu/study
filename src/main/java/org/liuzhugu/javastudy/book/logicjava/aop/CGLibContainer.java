package org.liuzhugu.javastudy.book.logicjava.aop;

import org.liuzhugu.javastudy.book.logicjava.annotation.Liuzhuzhu;
import org.liuzhugu.javastudy.book.logicjava.annotation.SingleInject;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

@Liuzhuzhu(info = {"cglib实现的容器","获取类实例的唯一入口,在该容器内封装一些操作,对所有类做统一的处理"})
public class CGLibContainer {
    /**
     * 入口
     * */
    //获取类实例,最终要的方法
    public static <T> T getInstance(Class<T> cls) {
        try {

            //获取实例,获取到的是增强的实例,即AOP
            T obj = createInstance(cls);

            //设置依赖关系,完成初始化
            //获取所有字段
            for (Field field : cls.getDeclaredFields()) {
                //判断是否使用了注解SingleInject
                if (field.isAnnotationPresent(SingleInject.class)) {
                    //修改访问权限
                    if (! field.isAccessible()) {
                        field.setAccessible(true);
                    }
                    //获取注解使用在什么类上
                    Class<?> fieldCls = field.getType();
                    //构造该类,设置依赖关系
                    //因为依赖关系是多层的,所以递归处理
                    field.set(obj,getInstance(fieldCls));
                }
            }
            return obj;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    //每个类的每个切点的方法列表
    static Map<Class<?>,Map<InterceptPoint, List<Method>>> interceptMethodsMap = new HashMap<>();

    //切点列表
    static  Class<?>[] aspects = new Class<?>[]{
            ServiceLogAspect.class,ExceptionAspect.class
    };

    //初始化
    static {
        init();
    }
    private static void init() {
        for (Class<?> cls : aspects) {
            Aspect aspect = cls.getAnnotation(Aspect.class);
            if (aspect != null) {
                //获取方法
                Method before = getMethod(cls,"before",new Class<?>[]{
                        //实例        方法          参数
                        Object.class,Method.class,Object[].class
                });
                Method after = getMethod(cls,"after",new Class<?>[]{
                        //实例        方法          参数            结果
                        Object.class,Method.class,Object[].class,Object.class
                });
                Method exception = getMethod(cls,"exception",new Class<?>[]{
                        //实例        方法          参数            异常
                        Object.class,Method.class,Object[].class,Throwable.class
                });
                //获取注解标记要增强的类
                Class<?>[] intercepttedArr = aspect.value();
                //填充每个类的每个切点的方法列表
                for (Class<?> interceptted : intercepttedArr) {
                    addInterceptMethod(interceptted,InterceptPoint.BEFORE,before);
                    addInterceptMethod(interceptted,InterceptPoint.AFTER,after);
                    addInterceptMethod(interceptted,InterceptPoint.EXCEPTION,exception);
                }
            }
        }
    }

    //设置什么类的什么切点使用在什么方法上
    private static void addInterceptMethod(Class<?> cls,InterceptPoint point,Method method) {
        if (method == null) {
            return;
        }
        //查看该类有什么切点
        Map<InterceptPoint,List<Method>> map = interceptMethodsMap.get(cls);
        if (map.isEmpty()) {
            map = new HashMap<>();
            interceptMethodsMap.put(cls,map);
        }
        List<Method> methods = map.get(point);
        if (methods.isEmpty()) {
            methods = new ArrayList<>();
            map.put(point,methods);
        }
        methods.add(method);
    }

    //获取方法
    private static Method getMethod(Class<?> cls,String methodName,Class<?>[] clss) {
        try {
            return cls.getMethod(methodName,clss);
        }catch (Exception e){
            throw new RuntimeException();
        }
    }

    //创建实例
    private static <T> T createInstance(Class<?> cls) throws InstantiationException,IllegalAccessException{
        //不是增强类直接创建
        if (! interceptMethodsMap.containsKey(cls)) {
            return (T) cls.newInstance();
        }
        //增强类,通过动态代理生成重写了原有方法的实现类
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(cls);
        //增强的实现逻辑
        enhancer.setCallback(new AspectInterceptor());
        return (T)enhancer.create();
    }

    //增强原有方法   传进来原有方法,按照固定模板增强原有方法
    //在该例中,在原有方法前执行before方法   之后执行after方法  出现异常时执行exception方法
    //方法和切面的关系维护在一个map里面
    static class AspectInterceptor implements MethodInterceptor {
        @Override
        public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            //某个类的某个方法需要执行那些before方法
            List<Method> beforeMethods = getInterceptMethods(object.getClass().getSuperclass()
                    ,InterceptPoint.BEFORE);
            for (Method m : beforeMethods) {
                m.invoke(null,new Object[]{object,method,args});
            }
            try {
                //调用原始方法
                Object result = methodProxy.invokeSuper(object,args);
                //执行after方法
                List<Method> afterMethods = getInterceptMethods(object.getClass().getSuperclass(),InterceptPoint.AFTER);
                for (Method m : afterMethods) {
                    m.invoke(null,new Object[]{object,method,args});
                }
                return result;
            }catch (Exception e) {
                //执行exception方法
                List<Method> exceptionMethods = getInterceptMethods(object.getClass().getSuperclass(),InterceptPoint.EXCEPTION);
                for (Method m : exceptionMethods) {
                    m.invoke(null,new Object[]{object,method,args});
                }
                //执行完之后继续抛异常,不破坏原有逻辑
                throw e;
            }
        }
    }

    //做一些判空处理
    static List<Method> getInterceptMethods(Class<?> cls,InterceptPoint point) {
        Map<InterceptPoint,List<Method>> map = interceptMethodsMap.get(cls);
        if (map == null) {
            return Collections.emptyList();
        }
        List<Method> methods = map.get(point);
        if (methods == null) {
            return Collections.emptyList();
        }
        return methods;
    }
}
