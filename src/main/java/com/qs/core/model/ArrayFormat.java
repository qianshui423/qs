package com.qs.core.model;


public enum ArrayFormat {
    INDICES("indices"), // "a[0]=b&a[1]=c"
    BRACKETS("brackets"), // "a[]=b&a[]=c"
    REPEAT("repeat"), // "a=b&a=c"
    COMMA("comma"); // "a=b,c"

    private String code;

    ArrayFormat(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
