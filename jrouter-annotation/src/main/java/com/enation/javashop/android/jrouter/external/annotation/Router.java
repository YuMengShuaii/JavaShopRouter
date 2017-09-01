package com.enation.javashop.android.jrouter.external.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 路由注解
 * @author LDD
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface Router {
    /**模块名称，非必填*/
    String module() default "";
    /**uri路径*/
    String path()   default "";
    /**页面名，非必填*/
    String name() default "undefined";
    /**附加*/
    int extras() default Integer.MIN_VALUE;
    /**优先级*/
    int priority() default -1;
}
