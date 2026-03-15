package io.sqlinsight.starter.aspect;

import io.sqlinsight.annotations.QueryLabel;
import io.sqlinsight.annotations.SlowQueryAlert;
import io.sqlinsight.core.context.QueryContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class SqlInsightAspect {

    @Around("@annotation(io.sqlinsight.annotations.QueryTrace)")
    public Object trackQuery(ProceedingJoinPoint joinPoint) throws Throwable {
        return wrapWithContext(joinPoint);
    }

    @Around("execution(* org.springframework.data.repository.Repository+.*(..))")
    public Object trackRepositoryCall(ProceedingJoinPoint joinPoint) throws Throwable {
        return wrapWithContext(joinPoint);
    }

    private Object wrapWithContext(ProceedingJoinPoint joinPoint) throws Throwable {
        QueryContext.startMethod(joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
        try {
            return joinPoint.proceed();
        } finally {
            QueryContext.endMethod();
        }
    }

    @Around("@annotation(queryLabel)")
    public Object labelQuery(ProceedingJoinPoint joinPoint, QueryLabel queryLabel) throws Throwable {
        String prev = QueryContext.getCurrentLabel();
        QueryContext.setLabel(queryLabel.value());
        try {
            return joinPoint.proceed();
        } finally {
            QueryContext.setLabel(prev);
        }
    }

    @Around("@annotation(slowQueryAlert)")
    public Object overrideThreshold(ProceedingJoinPoint joinPoint, SlowQueryAlert slowQueryAlert) throws Throwable {
        Integer prev = QueryContext.getSlowQueryThresholdOverride();
        QueryContext.setSlowQueryThresholdOverride(slowQueryAlert.threshold());
        try {
            return joinPoint.proceed();
        } finally {
            QueryContext.setSlowQueryThresholdOverride(prev);
        }
    }

    @Around("@annotation(io.sqlinsight.annotations.DisableQueryTracking)")
    public Object disableTracking(ProceedingJoinPoint joinPoint) throws Throwable {
        boolean prev = QueryContext.isTrackingDisabled();
        QueryContext.setTrackingDisabled(true);
        try {
            return joinPoint.proceed();
        } finally {
            QueryContext.setTrackingDisabled(prev);
        }
    }
}
