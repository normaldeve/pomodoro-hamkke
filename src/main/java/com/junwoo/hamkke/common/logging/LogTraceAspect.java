package com.junwoo.hamkke.common.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LogTraceAspect {

    // Controller
    @Around("execution(* com.junwoo.hamkke.domain..controller..*(..))")
    public Object controllerLog(ProceedingJoinPoint pjp) throws Throwable {
        return trace(pjp, "[Controller]");
    }

    // Service
    @Around("execution(* com.junwoo.hamkke.domain..service..*(..))")
    public Object serviceLog(ProceedingJoinPoint pjp) throws Throwable {
        return trace(pjp, "[Service]");
    }

    // Repository
    @Around("execution(* com.junwoo.hamkke.domain..repository..*(..))")
    public Object repositoryLog(ProceedingJoinPoint pjp) throws Throwable {
        return trace(pjp, "[Repository]");
    }

    private Object trace(ProceedingJoinPoint pjp, String layer) throws Throwable {
        int depth = LogTraceContext.increase();
        String method = pjp.getSignature().toShortString();
        String params = Arrays.toString(pjp.getArgs());

        try {
            log.info("{}{}{} {}", LogFormatter.request(depth), layer, method, params);

            Object result = pjp.proceed();

            log.info("{}{}{} return={}",
                    LogFormatter.response(depth),
                    layer,
                    method,
                    result
            );

            return result;

        } catch (Exception e) {
            log.error("{}{}{} ex={}",
                    LogFormatter.exception(depth),
                    layer,
                    method,
                    e.getMessage()
            );
            throw e;
        } finally {
            LogTraceContext.decrease();
        }
    }
}