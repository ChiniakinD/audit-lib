package ru.chiniakin.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.chiniakin.enums.LogSave;

/**
 * Конфигурационные свойства для настройки логирования аудита.
 * Свойства задаются в application.yml с использованием префикса "audit-log".
 *
 * @author ChiniakinD
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "audit-log")
public class AuditLogConfigurationProperty {

    private LogSave logSave;
    private String filePath;
    private boolean enable;

}
