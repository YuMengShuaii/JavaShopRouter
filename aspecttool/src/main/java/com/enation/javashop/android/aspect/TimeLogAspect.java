package com.enation.javashop.android.aspect;

/**
 * Created by LDD on 17/4/20.
 */

import android.util.Log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.concurrent.TimeUnit;

/**
 * 根据注解TimeLog自动添加打印方法耗代码，通过aop切片的方式在编译期间织入源代码中
 * 功能：自动打印方法的耗时
 */
@Aspect
public class TimeLogAspect {

    @Pointcut("execution(@com.enation.javashop.android.aspect.TimeLog * *(..))")//方法切入点
    public void methodAnnotated() {
    }

    @Pointcut("execution(@com.enation.javashop.android.aspect.TimeLog *.new(..))")//构造器切入点
    public void constructorAnnotated() {

    }

    @Around("methodAnnotated() || constructorAnnotated()")//在连接点进行方法替换
    public Object aroundJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        Log.e("IsLogin",joinPoint.getSignature().getName());
        return joinPoint.proceed();
    }
}
