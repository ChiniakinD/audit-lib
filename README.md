# Audit-lib

Стартер для логирования аудита - это стартер Spring Boot, который предоставляет функционал для логирования HTTP-запросов и методов. 
## Установка

1. Клонируйте репозиторий:
    ```bash
    git@github.com:ChiniakinD/audit-lib.git
    ```
2. Перейдите в директорию проекта:
    ```bash
    cd audit-lib
    ```
3. Установите зависимости и соберите проект с помощью Maven:
    ```bash
    ./mvnw clean install
    ```

## Работа с проектом

### 1. Добавьте зависимость стартера

Сначала добавьте зависимость стартер-проекта для логирования аудита в файл `pom.xml` вашего проекта Spring Boot.

#### Maven

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>Audit-log-aspect</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### 2. Необходимо отметить методы, которые вы хотите логировать, аннотацией `@AuditLog`:
### 3. Настройки логирования определяются в файле `application.yml`:
```yml
   audit-log:
       log-save: all # способ сохранения логов: `console`, `file` или `all` (по умолчанию `all`)
       file-path: logs/spring-boot-application.log # путь к файлу для сохранения логов
       enable: true # включить или отключить логирование
   ```
