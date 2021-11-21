package org.itrex.advice;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class SuccessfulRepoGetMethodExecutionAdvice {
    @Pointcut("within(org.itrex.repositories..*)")
    public void repositoryMethods() {}

    @Pointcut("execution(* *..get*(..))")
    public void getMethods() {}

    @Pointcut("repositoryMethods() && getMethods()")
    public void repositoryGetMethods() {}

    @AfterReturning(value = "repositoryGetMethods()", returning = "entities")
    public void checkRepoGetMethodExecution(JoinPoint joinPoint, Object entities) throws Throwable {
        String methodSignature = joinPoint.getSignature().toString();
        String result = String.format("\n* * * *\nMethod \"%s\" executed successfully.\nResult: \n%s\n* * * *\n",
                methodSignature, entities == null ? "null" : entities.toString());
        System.out.println(result);
        log.info(result);
    }
}