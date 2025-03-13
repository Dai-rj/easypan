package com.easypan.spring;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * ApplicationContextProvider类提供了访问Spring应用上下文的静态方法
 * 它实现了ApplicationContextAware接口，以便Spring框架在创建该类的实例时自动注入应用上下文
 */
@Component("applicationContextProvider")
public class ApplicationContextProvider implements ApplicationContextAware {
    // 日志对象，用于记录日志信息
    private static final Logger logger = LoggerFactory.getLogger(ApplicationContextProvider.class);

    /**
     * 上下文对象实例
     * -- GETTER --
     * 获取applicationContext
     */
    @Getter
    private static ApplicationContext applicationContext;

    /**
     * 设置应用上下文
     * 当Spring创建该类的实例时，自动调用此方法注入应用上下文
     *
     * @param applicationContext 应用上下文
     * @throws BeansException 如果设置过程中出现异常
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextProvider.applicationContext = applicationContext;
    }

    /**
     * 通过name获取 Bean.
     * 如果没有找到对应的Bean，则返回null，并记录错误日志
     *
     * @param name Bean的名称
     * @return 对应名称的Bean实例，如果没有找到则返回null
     */
    public static Object getBean(String name) {
        try {
            return getApplicationContext().getBean(name);
        } catch (NoSuchBeanDefinitionException e) {
            logger.error("获取bean异常", e);
            return null;
        }
    }

    /**
     * 通过class获取Bean.
     * 如果没有找到对应的Bean，则抛出BeanDefinitionStoreException异常
     *
     * @param clazz Bean的类类型
     * @param <T>   Bean的类型参数
     * @return 指定类型的Bean实例
     */
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    /**
     * 通过name,以及Clazz返回指定的Bean
     * 如果没有找到对应的Bean，则抛出BeanDefinitionStoreException异常
     *
     * @param name  Bean的名称
     * @param clazz Bean的类类型
     * @param <T>   Bean的类型参数
     * @return 指定名称和类型的Bean实例
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }
}
