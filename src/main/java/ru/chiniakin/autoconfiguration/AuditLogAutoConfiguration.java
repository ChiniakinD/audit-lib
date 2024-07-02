package ru.chiniakin.autoconfiguration;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import ru.chiniakin.aspect.AuditLogAspect;
import ru.chiniakin.interceptor.RequestResponseLoggingFilter;
import ru.chiniakin.property.AuditLogConfigurationProperty;
import ru.chiniakin.utils.LogOutputUtil;

/**
 * Автоконфигурация для логирования аудита.
 * Конфигурация активируется, если свойство "audit-log.enable" в true.
 */
@AutoConfiguration
@ConditionalOnProperty(value = "audit-log.enable", havingValue = "true")
@EnableConfigurationProperties(value = AuditLogConfigurationProperty.class)
public class AuditLogAutoConfiguration {

    /**
     * Создает бин {@link AuditLogAspect} если он отсутствует в контексте.
     */
    @Bean
    @ConditionalOnMissingBean
    public AuditLogAspect auditLogAspect(AuditLogConfigurationProperty auditLogConfigurationProperty, LogOutputUtil logOutputUtil) {
        return new AuditLogAspect(auditLogConfigurationProperty, logOutputUtil);
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

}
