package com.husttwj.imagecompress.util;

import java.lang.reflect.Method;
import java.util.HashMap;

public class ReflectUtils {

    private static HashMap<String, Method> sCacheMethod = new HashMap<>();

    private static Method sGetDeclaredField = null;

    private static Method sGetDeclaredMethod = null;

    static {
        try {
            sGetDeclaredMethod = Class.class.getDeclaredMethod("getDeclaredMethod", String.class, Class[].class);
            sGetDeclaredMethod.setAccessible(true);
        } catch (Throwable t) {
        }
        try {
            sGetDeclaredField = Class.class.getDeclaredMethod("getDeclaredField", String.class);
            sGetDeclaredField.setAccessible(true);
        } catch (Throwable t) {
        }
    }

    public static Method getClassMethod(Class clz, String methodName) {
        return getClassMethod(clz, methodName, (Class[]) null);
    }

    public static Method getClassMethod(Class clz, String methodName, Class<?>... clzs) {
        if (clz == null || methodName == null) {
            return null;
        }
        Method declaredMethod = null;
        final String cacheKey = clz.getName() + "." + methodName;
        if (sCacheMethod.containsKey(cacheKey)) {
            return sCacheMethod.get(cacheKey);
        }
        while (clz != null && clz != Object.class) {
            try {
                if (sGetDeclaredMethod != null) {
                    declaredMethod = (Method) sGetDeclaredMethod.invoke(clz, methodName, clzs);
                } else {
                    declaredMethod = (Method) clz.getDeclaredMethod(methodName, clzs);
                }
                declaredMethod.setAccessible(true);
            } catch (Throwable ignore) {
            }
            if (declaredMethod != null) {
                sCacheMethod.put(cacheKey, declaredMethod);
                return declaredMethod;
            }
            clz = clz.getSuperclass();
        }
        sCacheMethod.put(cacheKey, null);
        return null;
    }
}
