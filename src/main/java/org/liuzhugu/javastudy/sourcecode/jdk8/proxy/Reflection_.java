package org.liuzhugu.javastudy.sourcecode.jdk8.proxy;

import sun.misc.VM;
import sun.reflect.CallerSensitive;

import java.lang.reflect.Array;
import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.Map;

public class Reflection_ {
    private static volatile Map<Class_<?>, String[]> fieldFilterMap;
    private static volatile Map<Class_<?>, String[]> methodFilterMap;

    public Reflection_() {
    }

    @CallerSensitive
    public static native Class_<?> getCallerClass();

    /** @deprecated */
    @Deprecated
    public static native Class_<?> getCallerClass(int var0);

    public static native int getClassAccessFlags(Class_<?> var0);

    public static boolean quickCheckMemberAccess(Class_<?> var0, int var1) {
        return Modifier_.isPublic(getClassAccessFlags(var0) & var1);
    }

    public static void ensureMemberAccess(Class_<?> var0, Class_<?> var1, Object var2, int var3) throws IllegalAccessException {
        if (var0 != null && var1 != null) {
            if (!verifyMemberAccess(var0, var1, var2, var3)) {
                throw new IllegalAccessException("Class " + var0.getName() + " can not access a member of class " + var1.getName() + " with modifiers \"" + Modifier_.toString(var3) + "\"");
            }
        } else {
            throw new InternalError();
        }
    }

    public static boolean verifyMemberAccess(Class_<?> var0, Class_<?> var1, Object var2, int var3) {
        boolean var4 = false;
        boolean var5 = false;
        if (var0 == var1) {
            return true;
        } else {
            if (!Modifier_.isPublic(getClassAccessFlags(var1))) {
                var5 = isSameClassPackage(var0, var1);
                var4 = true;
                if (!var5) {
                    return false;
                }
            }

            if (Modifier_.isPublic(var3)) {
                return true;
            } else {
                boolean var6 = false;
                if (Modifier_.isProtected(var3) && isSubclassOf(var0, var1)) {
                    var6 = true;
                }

                if (!var6 && !Modifier_.isPrivate(var3)) {
                    if (!var4) {
                        var5 = isSameClassPackage(var0, var1);
                        var4 = true;
                    }

                    if (var5) {
                        var6 = true;
                    }
                }

                if (!var6) {
                    return false;
                } else {
                    if (Modifier_.isProtected(var3)) {
                        //Class_ var7 = var2 == null ? var1 : var2.getClass();
                        Class_ var7 = var1;
                        if (var7 != var0) {
                            if (!var4) {
                                var5 = isSameClassPackage(var0, var1);
                                var4 = true;
                            }

                            if (!var5 && !isSubclassOf(var7, var0)) {
                                return false;
                            }
                        }
                    }

                    return true;
                }
            }
        }
    }

    private static boolean isSameClassPackage(Class_<?> var0, Class_<?> var1) {
        return isSameClassPackage(var0.getClassLoader(), var0.getName(), var1.getClassLoader(), var1.getName());
    }

    private static boolean isSameClassPackage(ClassLoader var0, String var1, ClassLoader var2, String var3) {
        if (var0 != var2) {
            return false;
        } else {
            int var4 = var1.lastIndexOf(46);
            int var5 = var3.lastIndexOf(46);
            if (var4 != -1 && var5 != -1) {
                int var6 = 0;
                int var7 = 0;
                if (var1.charAt(var6) == '[') {
                    do {
                        ++var6;
                    } while(var1.charAt(var6) == '[');

                    if (var1.charAt(var6) != 'L') {
                        throw new InternalError("Illegal class name " + var1);
                    }
                }

                if (var3.charAt(var7) == '[') {
                    do {
                        ++var7;
                    } while(var3.charAt(var7) == '[');

                    if (var3.charAt(var7) != 'L') {
                        throw new InternalError("Illegal class name " + var3);
                    }
                }

                int var8 = var4 - var6;
                int var9 = var5 - var7;
                return var8 != var9 ? false : var1.regionMatches(false, var6, var3, var7, var8);
            } else {
                return var4 == var5;
            }
        }
    }

    static boolean isSubclassOf(Class_<?> var0, Class_<?> var1) {
        while(var0 != null) {
            if (var0 == var1) {
                return true;
            }

            var0 = var0.getSuperclass();
        }

        return false;
    }

    public static synchronized void registerFieldsToFilter(Class_<?> var0, String... var1) {
        fieldFilterMap = registerFilter(fieldFilterMap, var0, var1);
    }

    public static synchronized void registerMethodsToFilter(Class_<?> var0, String... var1) {
        methodFilterMap = registerFilter(methodFilterMap, var0, var1);
    }

    private static Map<Class_<?>, String[]> registerFilter(Map<Class_<?>, String[]> var0, Class_<?> var1, String... var2) {
        if (var0.get(var1) != null) {
            throw new IllegalArgumentException("Filter already registered: " + var1);
        } else {
            HashMap var3 = new HashMap(var0);
            var3.put(var1, var2);
            return var3;
        }
    }

    public static Field_[] filterFields(Class_<?> var0, Field_[] var1) {
        return fieldFilterMap == null ? var1 : (Field_[])((Field_[])filter(var1, (String[])fieldFilterMap.get(var0)));
    }

    public static Method_[] filterMethods(Class_<?> var0, Method_[] var1) {
        return methodFilterMap == null ? var1 : (Method_[])((Method_[])filter(var1, (String[])methodFilterMap.get(var0)));
    }

    private static Member_[] filter(Member_[] var0, String[] var1) {
        if (var1 != null && var0.length != 0) {
            int var2 = 0;
            Member_[] var3 = var0;
            int var4 = var0.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                Member_ var6 = var3[var5];
                boolean var7 = false;
                String[] var8 = var1;
                int var9 = var1.length;

                for(int var10 = 0; var10 < var9; ++var10) {
                    String var11 = var8[var10];
                    if (var6.getName() == var11) {
                        var7 = true;
                        break;
                    }
                }

                if (!var7) {
                    ++var2;
                }
            }

            var3 = (Member_[])((Member[]) Array.newInstance(var0[0].getClass(), var2));
            var4 = 0;
            Member_[] var14 = var0;
            int var15 = var0.length;

            for(int var16 = 0; var16 < var15; ++var16) {
                Member_ var17 = var14[var16];
                boolean var18 = false;
                String[] var19 = var1;
                int var20 = var1.length;

                for(int var12 = 0; var12 < var20; ++var12) {
                    String var13 = var19[var12];
                    if (var17.getName() == var13) {
                        var18 = true;
                        break;
                    }
                }

                if (!var18) {
                    var3[var4++] = var17;
                }
            }

            return var3;
        } else {
            return var0;
        }
    }

    public static boolean isCallerSensitive(Method_ var0) {
        ClassLoader var1 = var0.getDeclaringClass().getClassLoader();
        return !VM.isSystemDomainLoader(var1) && !isExtClassLoader(var1) ? false : var0.isAnnotationPresent(CallerSensitive.class);
    }

    private static boolean isExtClassLoader(ClassLoader var0) {
        for(ClassLoader var1 = ClassLoader.getSystemClassLoader(); var1 != null; var1 = var1.getParent()) {
            if (var1.getParent() == null && var1 == var0) {
                return true;
            }
        }

        return false;
    }

    static {
        HashMap var0 = new HashMap();
        var0.put(Reflection_.class, new String[]{"fieldFilterMap", "methodFilterMap"});
        var0.put(System.class, new String[]{"security"});
        var0.put(Class.class, new String[]{"classLoader"});
        fieldFilterMap = var0;
        methodFilterMap = new HashMap();
    }
}
