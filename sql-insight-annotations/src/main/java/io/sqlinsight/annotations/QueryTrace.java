package io.sqlinsight.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Tracks SQL queries executed within the annotated method.
 * Records execution duration and displays trace in dashboard.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryTrace {
    String value() default "";
}
