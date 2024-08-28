package ru.chiniakin.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Конфигурационные свойства для настроеу сервиса.
 * Свойства задаются в application.yml с использованием префикса "service".
 *
 * @author ChiniakinD
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "service")
public class ServiceProperties {

    private String name;

}
