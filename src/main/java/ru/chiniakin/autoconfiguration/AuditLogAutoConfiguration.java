package ru.chiniakin.autoconfiguration;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.chiniakin.aspect.AuditLogAspect;
import ru.chiniakin.interceptor.RequestResponseLoggingFilter;
import ru.chiniakin.service.kafka.LogService;
import ru.chiniakin.property.AuditLogConfigurationProperty;
import ru.chiniakin.property.ServiceProperties;
import ru.chiniakin.property.TopicProperties;
import ru.chiniakin.utils.LogOutputUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Автоконфигурация для логирования аудита.
 * Конфигурация активируется, если свойство "audit-log.enable" в true.
 */
@AutoConfiguration
@ConditionalOnProperty(value = "audit-log.enable", havingValue = "true")
@EnableConfigurationProperties(value = {AuditLogConfigurationProperty.class,
        TopicProperties.class,
        KafkaProperties.class,
        ServiceProperties.class})
public class AuditLogAutoConfiguration {

    /**
     * Создает бин {@link AuditLogAspect} если он отсутствует в контексте.
     */
    @Bean
    @ConditionalOnMissingBean
    public AuditLogAspect auditLogAspect(AuditLogConfigurationProperty auditLogConfigurationProperty,
                                         LogOutputUtil logOutputUtil,
                                         LogService logService,
                                         ServiceProperties serviceProperties) {
        return new AuditLogAspect(auditLogConfigurationProperty, logOutputUtil, logService, serviceProperties);
    }

    /**
     * Создает бин {@link RequestResponseLoggingFilter} если он отсутствует в контексте.
     */
    @Bean
    @ConditionalOnMissingBean
    public RequestResponseLoggingFilter requestResponseLoggingFilter(LogOutputUtil logOutputUtil) {
        return new RequestResponseLoggingFilter(logOutputUtil);
    }

    /**
     * Создает бин {@link LogOutputUtil} если он отсутствует в контексте.
     */
    @Bean
    @ConditionalOnMissingBean
    public LogOutputUtil logOutputUtil(AuditLogConfigurationProperty auditLogConfigurationProperty) {
        return new LogOutputUtil(auditLogConfigurationProperty);
    }

    @Bean
    @ConditionalOnMissingBean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    /**
     * Конфигурация продюсера для Kafka.
     *
     * @param kafkaProperties настроки Kafka.
     */
    @Bean
    @ConditionalOnMissingBean
    public Map<String, Object> producerConfigs(KafkaProperties kafkaProperties) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, kafkaProperties.getProducer().getAcks());
        props.put(ProducerConfig.RETRIES_CONFIG, kafkaProperties.getProducer().getRetries());
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "default-transactional-id");
        return props;
    }

    /**
     * Создает бин {@link ProducerFactory} если он отсутствует в контексте.
     *
     * @param kafkaProperties настроки Kafka.
     */
    @Bean
    @ConditionalOnMissingBean
    public ProducerFactory<String, Object> producerFactory(KafkaProperties kafkaProperties) {
        return new DefaultKafkaProducerFactory<>(producerConfigs(kafkaProperties));
    }

    /**
     * Создает бин {@link KafkaTemplate} если он отсутствует в контексте.
     */
    @Bean
    @ConditionalOnMissingBean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        KafkaTemplate<String, Object> kafkaTemplate = new KafkaTemplate<>(producerFactory);
        kafkaTemplate.setTransactionIdPrefix("default-transactional-id");
        return kafkaTemplate;
    }

    /**
     * Создает бин {@link KafkaAdmin} если он отсутствует в контексте.
     *
     * @param kafkaProperties настроки Kafka.
     */
    @Bean
    @ConditionalOnMissingBean
    public KafkaAdmin kafkaAdmin(KafkaProperties kafkaProperties) {
        return new KafkaAdmin(producerConfigs(kafkaProperties));
    }

    /**
     * Создает топик.
     *
     * @param topicProperties настройки топика.
     */
    @Bean
    @ConditionalOnMissingBean
    public NewTopic auditLogTopic(TopicProperties topicProperties) {
        return TopicBuilder.name(topicProperties.getSendLog())
                .partitions(1)
                .replicas(1)
                .build();
    }

    /**
     * Создает бин {@link LogService} если он отсутствует в контексте.
     *
     * @param kafkaTemplate   шаблон для отправки сообщений в Kafka.
     * @param topicProperties настройки топика.
     */
    @Bean
    @ConditionalOnMissingBean
    public LogService producer(KafkaTemplate<String, Object> kafkaTemplate, TopicProperties topicProperties) {
        return new LogService(kafkaTemplate, topicProperties);
    }

}
