package org.ChiniakinD.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Класс, который представляет параметры ответа.
 *
 * @author ChiniakinD
 */
@Getter
@AllArgsConstructor
public class HttpResponseAspect {

    private int status;
    private String body;

}
