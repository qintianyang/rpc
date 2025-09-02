package com.kk.annotation;

import com.kk.router.random.RandomRouter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
* 客户端注解
*/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface RpcReference {
    /**
     * 远程服务名
     * @return
     */
    String version() default "";
    /**
     * 超时时间
     * @return
     */
    long timeout() default 5000;
    /**
     * 服务名称
     * @return
     */
    String serviceName();
}