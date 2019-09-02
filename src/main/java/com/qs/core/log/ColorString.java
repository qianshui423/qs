package com.qs.core.log;

public enum ColorString {
    BLACK("\33[30;0m"),
    RED("\33[31;0m"),
    GREEN("\33[32;0m"),
    YELLOW("\33[33;0m"),
    BLUE("\33[34;0m");

    private String code;

    ColorString(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
