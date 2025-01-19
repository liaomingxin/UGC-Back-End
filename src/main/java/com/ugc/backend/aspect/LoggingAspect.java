package com.ugc.backend.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    /**
     * 定义一个切点，匹配所有 `com.ugc.backend.controller` 包下的类中的方法。
     */
    @Pointcut("execution(* com.ugc.backend.controller.*.*(..))")
    public void apiPointcut() {}

    /**
     * 环绕通知，记录 API 请求、响应和异常信息。
     *
     * @param joinPoint 连接点对象，提供方法签名和方法参数等信息。
     * @return 执行目标方法后的返回结果
     * @throws Throwable 可能抛出的异常
     */
    @Around("apiPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // 记录开始时间
        long startTime = System.nanoTime();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        
        // 获取当前 HTTP 请求对象
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
            .currentRequestAttributes()).getRequest();

        // 记录 API 请求信息
        log.info("API Request - Method: {} {} - Class: {} - Method: {} - Parameters: {}",
                request.getMethod(),
                request.getRequestURI(),
                className,
                methodName,
                Arrays.toString(joinPoint.getArgs()));

        try {
            // 执行目标方法
            Object result = joinPoint.proceed();

            // 记录 API 响应信息及执行时间
            log.info("API Response - Method: {} {} - Execution Time: {}ms - Result: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    (System.nanoTime() - startTime) / 1_000_000,  // 转换为毫秒
                    result);

            return result;
        } catch (Exception e) {
            // 记录异常信息
            log.error("API Error - Method: {} {} - Error Message: {} - Stack Trace: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    e.getMessage(),
                    Arrays.toString(e.getStackTrace()));

            // 重新抛出异常，确保正常的异常处理流程
            throw e;
        }
    }
}
