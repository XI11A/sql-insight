package io.sqlinsight.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a per-method slow query threshold.
 * Triggers alert if any query within the method exceeds the threshold.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SlowQueryAlert {
    int threshold() default 200;
}
