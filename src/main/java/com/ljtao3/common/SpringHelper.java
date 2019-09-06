package com.ljtao3.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

/**
 * Created by jimin on 15/11/21.
 */
@Slf4j
public class SpringHelper {

    private static ApplicationContext applicationContext;

    /**
     * @param applicationContext
     * @see ApplicationContextHelper
     * 在ApplicationContextHelper 中，将applicationContext赋值到这个类中
     */
    public static void setApplicationContext(ApplicationContext applicationContext) {
        SpringHelper.applicationContext = applicationContext;
    }

    public static <T> T popBean(Class<T> clazz) {
        if (applicationContext == null)
            return null;
        return applicationContext.getBean(clazz);
    }

    public static <T> T popBean(String name, Class<T> clazz) {
        if (applicationContext == null)
            return null;
        return applicationContext.getBean(name, clazz);
    }
}
