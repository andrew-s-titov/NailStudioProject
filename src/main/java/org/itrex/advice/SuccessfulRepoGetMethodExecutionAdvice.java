package org.itrex.advice;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

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
        String stringBuilder = "\n* * * * *\nMethod \"" +
                methodSignature +
                "\" executed successfully. Result:\n" +
                entities.toString() +
                "\n* * * * *\n";
        System.out.println(stringBuilder);
    }
}