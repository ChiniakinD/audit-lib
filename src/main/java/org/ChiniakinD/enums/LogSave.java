package org.ChiniakinD.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LogSave {

    FILE("file"),
    CONSOLE("console"),
    ALL("all");

    private final String value;

}
