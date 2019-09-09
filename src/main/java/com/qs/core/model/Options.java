package com.qs.core.model;

class Options {

    // 参数定界符
    public static final String DELIMITER = "&";
    // 不允许.为定界符
    public static final boolean ALLOW_DOTS = false;
    // 是否区分value为null
    public static final boolean STRICT_NULL_HANDLING = false;
    // 字符集(暂时只支持utf-8字符)
    public static final String CHARSET = Charset.UTF8.getCharset();

    private String delimiter;
    private boolean allowDots;
    private boolean strictNullHandling;
    private String charset;

    public Options(String delimiter, boolean allowDots, boolean strictNullHandling, String charset) {
        this.delimiter = delimiter;
        this.allowDots = allowDots;
        this.strictNullHandling = strictNullHandling;
        this.charset = charset;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public boolean isAllowDots() {
        return allowDots;
    }

    public boolean isStrictNullHandling() {
        return strictNullHandling;
    }

    public String getCharset() {
        return charset;
    }

    public static class Builder {
        private String delimiter = DELIMITER;
        private boolean allowDots = ALLOW_DOTS;
        private boolean strictNullHandling = STRICT_NULL_HANDLING;
        private String charset = CHARSET;

        public Builder setDelimiter(String delimiter) {
            this.delimiter = delimiter;
            return this;
        }

        public Builder setAllowDots(boolean allowDots) {
            this.allowDots = allowDots;
            return this;
        }

        public Builder setStrictNullHandling(boolean strictNullHandling) {
            this.strictNullHandling = strictNullHandling;
            return this;
        }

        public Options build() {
            return new Options(delimiter, allowDots, strictNullHandling, charset);
        }
    }

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

}
