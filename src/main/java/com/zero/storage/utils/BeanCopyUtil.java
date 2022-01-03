package com.zero.storage.utils;

import com.google.common.collect.Lists;
import com.zero.storage.annotation.CopySourceName;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public final class BeanCopyUtil {
    private BeanCopyUtil() {}

    private static final String[] IGNORE_PROPERTIES = {"id", "createdBy", "createTime", "updatedBy", "updateTime"};

    /**
     * ps：Test2 test2 = CopyBeanUtils.copyProperties(i1, Test2.class);
     *
     * @param source      原对象
     * @param targetClass 目标对象class
     * @param <T>
     * @return copy完后的对象
     */
    public static <T> T copyProperties(Object source, Class<T> targetClass) {

        T target = BeanUtils.instantiateClass(targetClass);
        copyProperties(source, target);
        return target;
    }

    public static <T> T copyProperties(Object source, Class<T> targetClass,String... ignore) {

        T target = BeanUtils.instantiateClass(targetClass);
        copyProperties(source, target,ignore);
        return target;
    }
    public static void copyProperties(Object source, Object target) throws BeansException {
        copyProperties(source, target, null, (String[])null);
    }

    public static void copyProperties(Object source, Object target, boolean ignore) throws BeansException {
        //ignore=true 表示 IGNORE_PROPERTIES 里面的属性不复制
        if (ignore) copyProperties(source, target, null, IGNORE_PROPERTIES);
        else copyProperties(source, target, (String[])null);
    }

    public static void copyProperties(Object source, Object target, Class<?> editable) throws BeansException {
        copyProperties(source, target, editable, (String[]) null);
    }

    /**
     * @param source           原对象
     * @param target           目标对象
     * @param ignoreProperties 排除某些不需要复制的属性
     * @throws BeansException
     */
    public static void copyProperties(Object source, Object target, String... ignoreProperties) throws BeansException {
        copyProperties(source, target, null, ignoreProperties);
    }

    private static void copyProperties(Object source, Object target, @Nullable Class<?> editable, @Nullable String... ignoreProperties) throws BeansException {

        Class<?> actualEditable = target.getClass();
        if (editable != null) {
            if (!editable.isInstance(target)) {
                throw new IllegalArgumentException("Target class [" + target.getClass().getName() +
                        "] not assignable to Editable class [" + editable.getName() + "]");
            }
            actualEditable = editable;
        }
        PropertyDescriptor[] targetPds = BeanUtils.getPropertyDescriptors(actualEditable);
        List<String> ignoreList = (ignoreProperties != null ? Arrays.asList(ignoreProperties) : null);

        HashMap<String, String> copySourceNameMap = new HashMap<>();

        for (Field field : actualEditable.getDeclaredFields()) {
            CopySourceName annotation = field.getAnnotation(CopySourceName.class);
            if (annotation != null) {
                copySourceNameMap.put(field.getName(), annotation.value());
            }
        }

        for (PropertyDescriptor targetPd : targetPds) {
            Method writeMethod = targetPd.getWriteMethod();
            String name = targetPd.getName();
            String sourceName = copySourceNameMap.get(name);
            if (sourceName != null) name = sourceName;
            if (writeMethod != null && (ignoreList == null || !ignoreList.contains(name))) {
                PropertyDescriptor sourcePd = BeanUtils.getPropertyDescriptor(source.getClass(), name);
                if (sourcePd != null) {
                    Method readMethod = sourcePd.getReadMethod();
                    if (readMethod != null &&
                            ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType())) {
                        try {
                            if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                                readMethod.setAccessible(true);
                            }
                            Object value = readMethod.invoke(source);
                            if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                writeMethod.setAccessible(true);
                            }
                            writeMethod.invoke(target, value);
                        } catch (Throwable ex) {
                            throw new FatalBeanException(
                                    "Could not copy property '" + name + "' from source to target", ex);
                        }
                    }
                }
            }
        }
    }

    public static <T,M> List<T> copyList(List<M> sources, Class<T> tClass){
        return Optional.of(sources)
                .orElse(Lists.newArrayList())
                .parallelStream().map(w->copyProperties(w,tClass))
                .collect(Collectors.toList());
    }

    public static <T,M> List<T> copyList(List<M> sources, Class<T> tClass, @Nullable String... ignoreProperties){
        return Optional.of(sources)
                .orElse(Lists.newArrayList())
                .parallelStream().map(w->copyProperties(w,tClass,ignoreProperties))
                .collect(Collectors.toList());
    }


    public static <T,M> Set<T> copySet(List<M> sources, Class<T> tClass, @Nullable String... ignoreProperties){
        return Optional.of(sources)
                .orElse(Lists.newArrayList())
                .parallelStream().map(w->copyProperties(w,tClass,ignoreProperties))
                .collect(Collectors.toSet());
    }

}
