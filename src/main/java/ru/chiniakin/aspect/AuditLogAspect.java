package ru.chiniakin.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.chiniakin.enums.LogLevel;
import ru.chiniakin.enums.LogSave;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.reflect.MethodSignature;
import ru.chiniakin.property.AuditLogConfigurationProperty;
import ru.chiniakin.utils.LogOutputUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.OffsetDateTime;

/**
 * Аспект, реализующий логирование методов, с аннотацией {@link AuditLog}.
 */
@Aspect
@Slf4j
@RequiredArgsConstructor
public class AuditLogAspect {

    private LogLevel logLevel;

    private AuditLogConfigurationProperty auditLogConfigurationProperty;

    private LogOutputUtil logOutputUtil;

    public AuditLogAspect(AuditLogConfigurationProperty auditLogConfigurationProperty, LogOutputUtil logOutputUtil) {
        this.auditLogConfigurationProperty = auditLogConfigurationProperty;
        this.logOutputUtil = logOutputUtil;
    }

    /**
     * Точка среза для методов с аннотацией {@link AuditLog}.
     */
    @Pointcut("@annotation(ru.chiniakin.aspect.AuditLog)")
    public void auditLogMethods() {
    }

    /**
     * Устанавливает уровень логирования метода, в зависимости от значения аннотации.
     *
     * @param auditLog .
     */
    @Before("@annotation(auditLog)")
    public void logLevelSetUp(AuditLog auditLog) {
        this.logLevel = auditLog.logLevel();
    }

    /**
     * Выполняет логирование после метода с аннотацией {@link AuditLog}.
     *
     * @param joinPoint точка соединения.
     */
    @After("auditLogMethods()")
    public void printMethodName(JoinPoint joinPoint) {
        outputLog(joinPoint, null);
    }

    /**
     * Выполняет логирование исключения в методе.
     *
     * @param joinPoint точка соединения.
     * @param e         возникающее исключение.
     */
    @AfterThrowing(pointcut = "auditLogMethods()", throwing = "e")
    public void printExceptions(JoinPoint joinPoint, Exception e) {
        outputLog(joinPoint, e);
    }

    /**
     * Создает и сохраняет лог.
     *
     * @param joinPoint точка соединения.
     * @param e         возникающее исключение.
     */
    private void outputLog(JoinPoint joinPoint, Exception e) {
        String log = generateLog(joinPoint, e);
        saveLog(log);
    }

    /**
     * Создает лог.
     *
     * @param joinPoint точка соединения.
     * @param e         возникающее исключение, при наличии.
     * @return строка лога.
     */
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

    /**
     * Создает строку аргументов метода.
     *
     * @param joinPoint точка соединения.
     * @return строка аргументов метода.
     */
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

    /**
     * Сохраняет лог в зависимости от указанного метода сохранения.
     *
     * @param log - строка лога.
     */
    private void saveLog(String log) {
        LogSave logSave = auditLogConfigurationProperty.getLogSave();
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

    /**
     * Выводит лог в консоль.
     *
     * @param logMessage - строка лога.
     */
    private void printToConsole(String logMessage) {
        switch (logLevel) {
            case INFO -> log.info(logMessage);
            case DEBUG -> log.debug(logMessage);
            case ERROR -> log.error(logMessage);
            case WARN -> log.warn(logMessage);
            default -> throw new RuntimeException();
        }
    }

    /**
     * Записывает в файл лог.
     *
     * @param log - строка лога.
     */
    private void writeToFile(String log) {
        createLogFile();
        try {
            FileWriter writer = new FileWriter(auditLogConfigurationProperty.getFilePath(), true);
            writer.write(getLogMetaData() + log);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Создает файл для записи логов, если он отсутствует.
     */
    private void createLogFile() {
        File file = new File(auditLogConfigurationProperty.getFilePath());
        if (file.exists()) {
            return;
        }
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        try {
            Files.createFile(Paths.get(auditLogConfigurationProperty.getFilePath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Возвращает метаданные лога, включая время и информационное сообщение о потоке.
     *
     * @return метаданные лога.
     */
    private String getLogMetaData() {
        return OffsetDateTime.now() + " " + formatThreadMessage();
    }

    /**
     * Создает информационное сообщение о потоке.
     *
     * @return сообщение об id потока, имени потока, класса в котором вызывается метод.
     */
    private String formatThreadMessage() {
        Thread currentThread = Thread.currentThread();
        long threadId = currentThread.getId();
        String threadName = currentThread.getName();
        String className = this.getClass().getName();
        return String.format("%s %d --- [%s] %s : ", logLevel, threadId, threadName, className);
    }

}
