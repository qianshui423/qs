package com.qs.core.model;

public enum ArrayFormat {
    INDICES("indices"),
    BRACKETS("brackets"),
    REPEAT("repeat"),
    COMMA("comma");

    private String code;

    ArrayFormat(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
