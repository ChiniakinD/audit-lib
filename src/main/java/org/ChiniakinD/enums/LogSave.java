package org.ChiniakinD.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Перечисление вариантов сохранения логов.
 */
@Getter
@RequiredArgsConstructor
public enum LogSave {

    FILE("file"),
    CONSOLE("console"),
    ALL("all");

    private final String value;

}
