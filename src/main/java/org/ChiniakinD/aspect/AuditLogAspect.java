package org.ChiniakinD.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ChiniakinD.enums.LogLevel;
import org.ChiniakinD.enums.LogSave;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.OffsetDateTime;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AuditLogAspect {

    private LogLevel logLevel;

    @Value("${audit-log.file-path}")
    private String path;

    @Value("${audit-log.log-save}")
    private LogSave logSave;

    @Pointcut("@annotation(org.ChiniakinD.aspect.AuditLog)")
    public void auditLogMethods() {
    }

    @Before("@annotation(auditLog)")
    public void logLevelSetUp(AuditLog auditLog) {
        this.logLevel = auditLog.logLevel();
    }

    @After("auditLogMethods()")
    public void printMethodName(JoinPoint joinPoint) {
        outputLog(joinPoint, null);
    }

    @AfterThrowing(pointcut = "auditLogMethods()", throwing = "e")
    public void printExceptions(JoinPoint joinPoint, Exception e) {
        outputLog(joinPoint, e);
    }

    private void outputLog(JoinPoint joinPoint, Exception e) {
        String log = generateLog(joinPoint, e);
        saveLog(log);
    }

    private String generateLog(JoinPoint joinPoint, Exception e) {
        Method joinPointMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();
        String methodName = joinPointMethod.getName();
        Object object = joinPointMethod.getReturnType();
        StringBuilder argsString = getStringArgs(joinPoint);
        String log;
        if (e == null) {
            log = methodName + " Input Arguments: " + argsString + " Return type: " + object + "\n";
        } else {
            log = methodName + " Input Arguments: " + argsString + " Return type: " + object
                    + " Exception message: " + e.getMessage() + "\n";
        }
        return log;
    }

    private StringBuilder getStringArgs(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        StringBuilder argsString = new StringBuilder();
        for (Object arg : args) {
            String typeOfArgument = arg.getClass().getName();
            argsString.append(typeOfArgument).append(": ");
            argsString.append(arg).append(", ");
        }
        return argsString;
    }

    private void saveLog(String log) {
        switch (logSave) {
            case CONSOLE -> printToConsole(log);
            case FILE -> writeToFile(log);
            case ALL -> {
                printToConsole(log);
                writeToFile(log);
            }
            default -> throw new RuntimeException();
        }
    }

    private void printToConsole(String logMessage) {
        switch (logLevel) {
            case INFO -> log.info(logMessage);
            case DEBUG -> log.debug(logMessage);
            case ERROR -> log.error(logMessage);
            case WARN -> log.warn(logMessage);
            default -> throw new RuntimeException();
        }
    }

    private void writeToFile(String log) {
        createLogFile();
        try {
            FileWriter writer = new FileWriter(path, true);
            writer.write(getLogMetaData() + log);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createLogFile() {
        File file = new File(path);
        if (file.exists()) {
            return;
        }
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        try {
            Files.createFile(Paths.get(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getLogMetaData() {
        return OffsetDateTime.now() + " " + formatThreadMessage();
    }

    private String formatThreadMessage() {
        Thread currentThread = Thread.currentThread();
        long threadId = currentThread.getId();
        String threadName = currentThread.getName();
        String className = this.getClass().getName();
        return String.format("%s %d --- [%s] %s : ", logLevel, threadId, threadName, className);
    }

}
