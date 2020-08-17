package com.potato.tools;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * create by Potato
 * create time 2020/8/15
 * Description：
 */
public class ReflectTool {

    /**
     * 从 instance 到其父类 找 name 属性
     *
     * @param path 类名全路径
     * @param name 属性名
     * @return 属性
     */
    public static Field findField(String path, String name) throws NoSuchFieldException, ClassNotFoundException {
        for (Class<?> clazz = Class.forName(path); clazz != null; clazz = clazz.getSuperclass()) {
            try {
                //查找当前类的 属性(不包括父类)
                Field field = clazz.getDeclaredField(name);

                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                return field;
            } catch (NoSuchFieldException e) {
                // ignore and search next
            }
        }
        throw new NoSuchFieldException("Field " + name + " not found in " + path);
    }

    /**
     * 从 instance 到其父类 找  name 方法
     *
     * @param path 类名全路径
     * @param name 方法名
     * @return 方法
     */
    public static Method findMethod(String path, String name, Class<?>... parameterTypes)
            throws NoSuchMethodException, ClassNotFoundException {
        for (Class<?> clazz = Class.forName(path); clazz != null; clazz = clazz.getSuperclass()) {
            try {
                Method method = clazz.getDeclaredMethod(name, parameterTypes);

                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }

                return method;
            } catch (NoSuchMethodException e) {
                // ignore and search next
            }
        }
        throw new NoSuchMethodException("Method "
                + name
                + " with parameters "
                + Arrays.asList(parameterTypes)
                + " not found in " + path);
    }
}
