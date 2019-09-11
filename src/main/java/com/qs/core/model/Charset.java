package com.qs.core.model;

public enum Charset {

    UTF8("utf-8");

    private String value;

    Charset(String value) {
        this.value = value;
    }

    public String getCharset() {
        return value;
    }
}
