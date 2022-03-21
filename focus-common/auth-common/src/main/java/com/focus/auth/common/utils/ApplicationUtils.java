package com.focus.auth.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 实现ApplicationContextAware接口，获取ApplicationContext实例，便于IoC容器中组件的获取
 *
 */
public class ApplicationUtils implements ApplicationContextAware {

    public static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        ApplicationUtils.applicationContext=context;
    }
}
