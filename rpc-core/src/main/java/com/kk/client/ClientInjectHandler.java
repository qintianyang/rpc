package com.kk.client;

import com.kk.annotation.RpcReference;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

@Configuration
@Slf4j
public class ClientInjectHandler implements BeanPostProcessor {
    /**
     * 在 Spring 容器完成 Bean 的初始化之后，对 Bean 进行后处理。
     * 如果发现 Bean 的字段上有 @RpcReference 注解，会动态代理这些字段。
     *
     * @param bean     Spring 容器中初始化后的 Bean
     * @param beanName Bean 的名称
     * @return 处理后的 Bean
     * @throws BeansException 如果发生错误
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> aClass = bean.getClass();
        Field[] fields = aClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(RpcReference.class)) {
                try {
                    Class<?> type = field.getType();
                    field.setAccessible(true);
                    System.out.println(field.getType());
                    field.set(bean, Proxy.newProxyInstance(
                            Thread.currentThread().getContextClassLoader(),
                            // new Class[]{field.getType()},
                            new Class[]{type},
                            new ClientProxy()
                    ));
                    field.setAccessible(false);
                } catch (IllegalAccessException e) {
                    log.error("客户端实例化错误 {}", e.getMessage());
                }
            }
        }
        return bean;
    }
}