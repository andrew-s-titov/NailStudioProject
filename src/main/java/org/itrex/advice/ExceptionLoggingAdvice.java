package org.itrex.advice;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ExceptionLoggingAdvice {

    @Around("execution(* org.itrex.repository..*.*(..))")
    public Object checkIfRepoMethodFailedWithException(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            String method = joinPoint.getSignature().toString();
            log.error("\n* * *\nFailed to execute method \"{}\".\nException: {}\n* * *", method, throwable.getClass());
            throwable.printStackTrace();
            throw throwable; // to avoid test failing
        }
        return result;
    }
}