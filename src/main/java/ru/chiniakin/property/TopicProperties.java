package ru.chiniakin.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Конфигурационные свойства для настроек топика Kafka.
 * Свойства задаются в application.yml с использованием префикса "topic".
 *
 * @author ChiniakinD
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "topic")
public class TopicProperties {

    private String sendLog;

}
