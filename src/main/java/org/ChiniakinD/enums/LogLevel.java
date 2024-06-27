package org.ChiniakinD.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LogLevel {

    INFO("info"),
    DEBUG("debug"),
    WARN("warn"),
    ERROR("error");

    private final String value;

}
