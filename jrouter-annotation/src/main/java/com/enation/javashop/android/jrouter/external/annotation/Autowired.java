package com.enation.javashop.android.jrouter.external.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  参数注入注解
 *  @author LDD
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.CLASS)
public @interface Autowired {
    /**多个同类型参数时，使用name区分*/
    String name() default "";

    /**是否为必要参数*/
    boolean required() default false;

    /**排序*/
    String desc() default "No desc.";
}
