package ru.chiniakin.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.chiniakin.utils.LogOutputUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * Класс, обрабатывающий все исключения по приложению.
 *
 * @author ChiniakinD
 */
@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final LogOutputUtil logOutputUtil;

    /**
     * Метод, обрабатывающий все исключения по приложению.
     *
     * @param e       выброшенное исключение.
     * @param request запрос.
     * @return ResponseEntity - включает сообщение об ошибке и статус.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception e, WebRequest request) {
        String logMessage = String.format("Exception: %s, message: %s, request: %s",
                e.getClass().getName(), e.getMessage(), request.getDescription(false));

        logOutputUtil.saveLog(logMessage);

        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
