package com.enation.javashop.android.jrouter.external.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 拦截器注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface Interceptor {

    /**拦截器优先级*/
    int priority();

    /**拦截器名称*/
    String name() default "Default";
}