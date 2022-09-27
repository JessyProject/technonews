package com.jessy.technonews.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ServiceMonitor {
    @Pointcut("execution(* com.jessy.technonews.service.*.*(..))")
    public void methodCall() {}

    @AfterReturning("methodCall()")
    public void log(JoinPoint joinPoint) {
        log.info(joinPoint.getSignature().getName() + " called...");
    }

    @AfterThrowing(pointcut = "methodCall()", throwing = "e")
    public void log(JoinPoint joinPoint, Throwable e) {
       log.info("Return of {} with an exception {}",
                joinPoint.toShortString(),
                e.getClass().getSimpleName());
    }
}
