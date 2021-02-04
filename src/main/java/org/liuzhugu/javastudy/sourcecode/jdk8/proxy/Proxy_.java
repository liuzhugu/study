package org.liuzhugu.javastudy.sourcecode.jdk8.proxy;

import sun.misc.VM;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.reflect.misc.ReflectUtil;
import sun.security.util.SecurityConstants;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;

public class Proxy_ implements java.io.Serializable {

    /**
     *                            核心方法
     */

    /**
     *    生成代理类
     */
    @CallerSensitive
    public static Object newProxyInstance(ClassLoader loader,
                                          Class_<?>[] interfaces,
                                          InvocationHandler_ h)
            throws IllegalArgumentException
    {
        Objects.requireNonNull(h);

        final Class_<?>[] intfs = interfaces.clone();
        /*
         * 从缓存中查找指定代理类
         */
        Class_<?> cl = getProxyClass0(loader, intfs);

        try {
            //找到代理类后获取去构造方法
            //final Constructor<?> cons = cl.getConstructor(constructorParams);
            final Constructor_<?> cons = null;
            final InvocationHandler_ ih = h;
            /*
             * 通过指定的处理程序  调用其构造方法
             */
            return cons.newInstance(new Object[]{h});
        }
        catch (InstantiationException e) {
            throw new InternalError(e.toString(), e);
        } catch (InvocationTargetException e) {
            Throwable t = e.getCause();
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            } else {
                throw new InternalError(t.toString(), t);
            }
        }
    }

    private static Class_<?> getProxyClass0(ClassLoader loader,
                                            Class_<?>... interfaces) {
        //从缓存中获取代理类  而缓存中的代理类是在代理工厂中生成的
        return Proxy_ClassCache.get(loader, interfaces);
    }

    /**
     * 代理类工厂
     */
    private static final class ProxyClassFactory
            implements BiFunction<ClassLoader, Class_<?>[], Class_<?>>
    {
        // 生成的代理类的前缀
        private static final String ProxyClassNamePrefix = "$Proxy_";

        @Override
        public Class_<?> apply(ClassLoader loader, Class_<?>[] interfaces) {

            Map<Class_<?>, Boolean> interfaceSet = new IdentityHashMap<>(interfaces.length);
            for (Class_<?> intf : interfaces) {
                /**
                 * 验证类加载器是否将此接口的名称解析为同一Class对象
                 */
                Class<?> interfaceClass = null;
                try {
                    interfaceClass = Class.forName(intf.getName(), false, loader);
                } catch (ClassNotFoundException e) {
                }

            }

            String ProxyPkg = null;     // package to define Proxy_ class in
            int accessFlags = Modifier.PUBLIC | Modifier.FINAL;

            //将非公共的接口修改权限  让其能在外界被访问到(代码已删)

            if (ProxyPkg == null) {
                // if no non-public Proxy_ interfaces, use com.sun.Proxy_ package
                ProxyPkg = ReflectUtil_.PROXY_PACKAGE + ".";
            }
            /*
             * 得到类名
             */
            long num = nextUniqueNumber.getAndIncrement();
            String ProxyName = ProxyPkg + ProxyClassNamePrefix + num;

            /*
             * 生成指定的代理类的字节流
             */
            byte[] ProxyClassFile = ProxyGenerator_.generateProxyClass(
                    ProxyName, interfaces, accessFlags);
            try {
                //根据字节流生成class
                return defineClass0(loader, ProxyName,
                        ProxyClassFile, 0, ProxyClassFile.length);
            } catch (ClassFormatError e) {
                throw new IllegalArgumentException(e.toString());
            }
        }
        // next number to use for generation of unique Proxy_ class names
        private static final AtomicLong nextUniqueNumber = new AtomicLong();
    }

    private static final long serialVersionUID = -2222568056686623797L;

    /** 代理类构造方法的参数类型 */
    private static final Class<?>[] constructorParams =
            { InvocationHandler_.class };

    /**
     * 缓存代理类
     */
    private static final WeakCache_<ClassLoader, Class_<?>[], Class_<?>>
            Proxy_ClassCache = new WeakCache_<>(new KeyFactory(), new ProxyClassFactory());

    /**
     *  调用处理器
     */
    protected InvocationHandler h;

    /**
     * Prohibits instantiation.
     */
    private Proxy_() {
    }

    protected Proxy_(InvocationHandler h) {
        Objects.requireNonNull(h);
        this.h = h;
    }


    //获取代理类
    @CallerSensitive
    public static Class_<?> getProxyClass(ClassLoader loader,
                                          Class_<?>... interfaces)
            throws IllegalArgumentException
    {
        final Class_<?>[] intfs = interfaces.clone();
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            checkProxyAccess(Reflection_.getCallerClass(), loader, intfs);
        }

        return getProxyClass0(loader, intfs);
    }


    private static void checkProxyAccess(Class_<?> caller,
                                         ClassLoader loader,
                                         Class_<?>... interfaces)
    {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            ClassLoader ccl = caller.getClassLoader();
            if (VM.isSystemDomainLoader(loader) && !VM.isSystemDomainLoader(ccl)) {
                sm.checkPermission(SecurityConstants.GET_CLASSLOADER_PERMISSION);
            }
            //ReflectUtil.checkProxy_PackageAccess(ccl, interfaces);
        }
    }



    /*
     * a key used for Proxy_ class with 0 implemented interfaces
     */
    private static final Object key0 = new Object();

    /*
     * Key1 and Key2 are optimized for the common use of dynamic proxies
     * that implement 1 or 2 interfaces.
     */

    /*
     * a key used for Proxy_ class with 1 implemented interface
     */
    private static final class Key1 extends WeakReference_<Class_<?>> {
        private final int hash;

        Key1(Class_<?> intf) {
            super(intf);
            this.hash = intf.hashCode();
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            Class_<?> intf;
            return this == obj ||
                    obj != null &&
                            obj.getClass() == Key1.class &&
                            (intf = get()) != null &&
                            intf == ((Key1) obj).get();
        }
    }

    /*
     * a key used for Proxy_ class with 2 implemented interfaces
     */
    private static final class Key2 extends WeakReference_<Class_<?>> {
        private final int hash;
        private final WeakReference_<Class_<?>> ref2;

        Key2(Class_<?> intf1, Class_<?> intf2) {
            super(intf1);
            hash = 31 * intf1.hashCode() + intf2.hashCode();
            ref2 = new WeakReference_<Class_<?>>(intf2);
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            Class_<?> intf1, intf2;
            return this == obj ||
                    obj != null &&
                            obj.getClass() == Key2.class &&
                            (intf1 = get()) != null &&
                            intf1 == ((Key2) obj).get() &&
                            (intf2 = ref2.get()) != null &&
                            intf2 == ((Key2) obj).ref2.get();
        }
    }

    /*
     * a key used for Proxy_ class with any number of implemented interfaces
     * (used here for 3 or more only)
     */
    private static final class KeyX {
        private final int hash;
        private final WeakReference_<Class_<?>>[] refs;

        @SuppressWarnings("unchecked")
        KeyX(Class_<?>[] interfaces) {
            hash = Arrays.hashCode(interfaces);
            refs = (WeakReference_<Class_<?>>[])new WeakReference_<?>[interfaces.length];
            for (int i = 0; i < interfaces.length; i++) {
                refs[i] = new WeakReference_<>(interfaces[i]);
            }
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj ||
                    obj != null &&
                            obj.getClass() == KeyX.class &&
                            equals(refs, ((KeyX) obj).refs);
        }

        private static boolean equals(WeakReference_<Class_<?>>[] refs1,
                                      WeakReference_
                                              <Class_<?>>[] refs2) {
            if (refs1.length != refs2.length) {
                return false;
            }
            for (int i = 0; i < refs1.length; i++) {
                Class_<?> intf = refs1[i].get();
                if (intf == null || intf != refs2[i].get()) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * A function that maps an array of interfaces to an optimal key where
     * Class objects representing interfaces are weakly referenced.
     */
    private static final class KeyFactory
            implements BiFunction<ClassLoader, Class_<?>[], Object>
    {
        @Override
        public Object apply(ClassLoader classLoader, Class_<?>[] interfaces) {
            switch (interfaces.length) {
                case 1: return new Key1(interfaces[0]); // the most frequent
                case 2: return new Key2(interfaces[0], interfaces[1]);
                case 0: return key0;
                default: return new KeyX(interfaces);
            }
        }
    }





    private static void checkNewProxyPermission(Class_<?> caller, Class_<?> ProxyClass) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            if (ReflectUtil_.isNonPublicProxyClass(ProxyClass)) {
                ClassLoader ccl = caller.getClassLoader();
                ClassLoader pcl = ProxyClass.getClassLoader();

                // do permission check if the caller is in a different runtime package
                // of the Proxy_ class
                int n = ProxyClass.getName().lastIndexOf('.');
                String pkg = (n == -1) ? "" : ProxyClass.getName().substring(0, n);

                n = caller.getName().lastIndexOf('.');
                String callerPkg = (n == -1) ? "" : caller.getName().substring(0, n);

                if (pcl != ccl || !pkg.equals(callerPkg)) {
                    sm.checkPermission(new ReflectPermission("newProxy_InPackage." + pkg));
                }
            }
        }
    }


    public static boolean isProxyClass(Class_<?> cl) {
        //return class.isAssignableFrom(cl) && Proxy_ClassCache.containsValue(cl);
        return true;
    }


    @CallerSensitive
    public static InvocationHandler getInvocationHandler(Object Proxy)
            throws IllegalArgumentException
    {
        /*
         * Verify that the object is actually a Proxy_ instance.
         */
//        if (!isProxyClass(Proxy_.getClass())) {
//            throw new IllegalArgumentException("not a Proxy_ instance");
//        }

        final Proxy_ p = (Proxy_) Proxy;
        final InvocationHandler ih = p.h;
        if (System.getSecurityManager() != null) {
            Class<?> ihClass = ih.getClass();
            Class<?> caller = Reflection.getCallerClass();
            if (ReflectUtil.needsPackageAccessCheck(caller.getClassLoader(),
                    ihClass.getClassLoader()))
            {
                ReflectUtil.checkPackageAccess(ihClass);
            }
        }

        return ih;
    }

    private static native Class_<?> defineClass0(ClassLoader loader, String name,
                                                byte[] b, int off, int len);
}



