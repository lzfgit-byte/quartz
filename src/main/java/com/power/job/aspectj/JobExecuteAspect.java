package com.power.job.aspectj;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class JobExecuteAspect {

    @Pointcut("execution(public void com.power.job.job.JobExecute.execute(..))")
    public void logInfo() {
    }

    @Before("logInfo()")
    public void before() {
    }

//    @Around("logInfo()")
//    public Object invoke(ProceedingJoinPoint point) throws Throwable {
//        Object proceed = point.proceed();
//        return proceed;
//    }

}
