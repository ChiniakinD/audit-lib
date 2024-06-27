package org.ChiniakinD.aspect;

import org.ChiniakinD.enums.LogLevel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AuditLog {

    LogLevel logLevel() default LogLevel.INFO;

}
