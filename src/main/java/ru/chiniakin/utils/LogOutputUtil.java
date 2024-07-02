package ru.chiniakin.utils;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.chiniakin.enums.LogSave;
import ru.chiniakin.model.HttpRequestAspect;
import ru.chiniakin.model.HttpResponseAspect;
import ru.chiniakin.property.AuditLogConfigurationProperty;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.OffsetDateTime;

/**
 * Класс для логирования данных запросов и ответов.
 *
 * @author ChiniakinD
 */
@Slf4j
@Setter
@RequiredArgsConstructor
public class LogOutputUtil {

    private HttpRequestAspect httpRequestAspect;

    private HttpResponseAspect httpResponseAspect;

    private AuditLogConfigurationProperty auditLogConfigurationProperty;

    public LogOutputUtil(AuditLogConfigurationProperty auditLogConfigurationProperty) {
        this.auditLogConfigurationProperty = auditLogConfigurationProperty;
    }

    /**
     * Сохраняет лог в зависимости от указанного метода сохранения.
     *
     * @param logMessage - строка лога.
     */
    public void saveLog(String logMessage) {
        LogSave logSave = auditLogConfigurationProperty.getLogSave();
        switch (logSave) {
            case CONSOLE:
                log.info(logMessage);
                break;
            case FILE:
                writeToFile(logMessage);
                break;
            case ALL:
                log.info(logMessage);
                writeToFile(logMessage);
                break;
            default:
                throw new RuntimeException();
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
            FileWriter writeFile = new FileWriter(auditLogConfigurationProperty.getFilePath(), true);
            writeFile.write(log + System.lineSeparator());
            writeFile.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Создает сообщение лога на основе данных запроса и ответа.
     *
     * @return строку лога.
     */
    public String createHttpRequestLogMessage() {
        OffsetDateTime time = OffsetDateTime.now();
        return String.format(time + " request method: %s, request: %s, request body: %s, response status: %d, response body: %s",
                httpRequestAspect.getMethod(), httpRequestAspect.getUri(), httpRequestAspect.getBody(),
                httpResponseAspect.getStatus(), httpResponseAspect.getBody());
    }

    /**
     * Создает файл для записи логов, если он отсутствует.
     */
    public void createLogFile() {
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

}
