package itma.smesharikiback.infrastructure.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("within(itma.smesharikiback.presentation.controller..*)")
    public Object logControllers(ProceedingJoinPoint joinPoint) throws Throwable {
        return logExecution(joinPoint, "controller");
    }

    @Around("within(itma.smesharikiback.application.service..*)")
    public Object logServices(ProceedingJoinPoint joinPoint) throws Throwable {
        return logExecution(joinPoint, "service");
    }

    private Object logExecution(ProceedingJoinPoint joinPoint, String layer) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String signature = className + "." + methodName;
        int argsCount = joinPoint.getArgs() == null ? 0 : joinPoint.getArgs().length;

        long start = System.currentTimeMillis();
        log.info("Enter {} {} argsCount={}", layer, signature, argsCount);
        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - start;
            log.info("Exit {} {} durationMs={}", layer, signature, duration);
            return result;
        } catch (Throwable ex) {
            long duration = System.currentTimeMillis() - start;
            log.warn("Error in {} {} durationMs={} message={}", layer, signature, duration, ex.getMessage());
            throw ex;
        }
    }
}
