package com.kk.annotation;

// import com.kk.client.ClientInjectHandler;
import com.kk.client.ClientInjectHandler;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Import(ClientInjectHandler.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableRpcClient {
}