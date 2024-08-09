package ru.chiniakin.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.chiniakin.enums.LogSave;

/**
 * Конфигурационные свойства для настройки логирования аудита.
 * Свойства задаются в application.yml с использованием префикса "audit-log".
 */
@ConfigurationProperties(prefix = "audit-log")
public class AuditLogConfigurationProperty {

    private LogSave logSave;
    private String filePath;
    private boolean enable;

    /**
     * @return способ сохранения логов
     */
    public LogSave getLogSave() {
        return logSave;
    }

    /**
     * Устанавливает способ сохранения логов.
     *
     * @param logSave способ сохранения логов
     */
    public void setLogSave(LogSave logSave) {
        this.logSave = logSave;
    }

    /**
     * @return путь к файлу
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Устанавливает путь к файлу для сохранения логов.
     *
     * @param filePath путь к файлу
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * @return true, если логирование включено, иначе false
     */
    public boolean isEnable() {
        return enable;
    }

    /**
     * Включает или отключает логирование.
     *
     * @param enable true для включения логирования, false для отключения
     */
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

}
