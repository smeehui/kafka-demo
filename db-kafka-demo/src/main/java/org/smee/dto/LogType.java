package org.smee.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LogType {
    UPDATE("u"), CREATE ("c"), DELETE("d");
    private final String text;
    public static LogType fromString(String text) {
        for (LogType type : LogType.values()) {
            if (type.text.equals(text)) {
                return type;
            }
        }
        return null;
    }
}
