package ru.chiniakin.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Класс, который представляет параметры запроса.
 *
 * @author ChiniakinD
 */
@Getter
@AllArgsConstructor
public class HttpRequestAspect {

    private String method;
    private String uri;
    private String body;

}
