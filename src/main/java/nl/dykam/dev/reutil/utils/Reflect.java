package nl.dykam.dev.reutil.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

public class Reflect {
    public static <T extends Annotation> Map<Method, T> getAnnotatedMethods(Object instance, Class<T> annotationType) {
        Set<Method> methods = new HashSet<>();
        methods.addAll(Arrays.asList(instance.getClass().getDeclaredMethods()));
        Map<Method, T> mapped = new HashMap<>();
        for (Method method : methods) {
            T annotation = method.getAnnotation(annotationType);
            if (annotation == null)
                continue;
            if(method.isSynthetic())
                continue;
            if(!method.isAccessible())
                method.setAccessible(true);
            mapped.put(method, annotation);
        }
        return mapped;
    }
}
