package ru.chiniakin.service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;
import ru.chiniakin.model.kafka.LogModel;
import ru.chiniakin.property.TopicProperties;

/**
 * Сервис для отправки сообщений логов Kafka.
 *
 * @author ChiniakinD
 */
@Slf4j
@RequiredArgsConstructor
public class LogService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private TopicProperties topicProperties;

    public LogService(KafkaTemplate<String, Object> kafkaTemplate, TopicProperties topicProperties) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicProperties = topicProperties;
    }

    /**
     * Отправление модели логов в топик Kafka.
     *
     * @param logModel модель логов.
     */
    @Transactional
    public void sendLogModel(LogModel logModel) {
        kafkaTemplate.send(topicProperties.getSendLog(), logModel);
        log.info("Cообщение отправлено " + topicProperties.getSendLog());
    }

}
