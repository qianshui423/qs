package com.qs.core.model;

class Options {

    // 参数定界符
    public static final String DELIMITER = "&";
    // 不允许 . 为 path 定界符
    public static final boolean ALLOW_DOTS = false;
    // 是否区分 value 为 null
    public static final boolean STRICT_NULL_HANDLING = false;
    // 字符集(暂时只支持utf-8字符)
    public static final String CHARSET = Charset.UTF8.getCharset();

    private String delimiter = DELIMITER;
    private String charset = CHARSET;
    private boolean allowDots;
    private boolean strictNullHandling;

    public Options(boolean allowDots, boolean strictNullHandling) {
        this.allowDots = allowDots;
        this.strictNullHandling = strictNullHandling;
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
        private boolean allowDots = ALLOW_DOTS;
        private boolean strictNullHandling = STRICT_NULL_HANDLING;

        public Builder setAllowDots(boolean allowDots) {
            this.allowDots = allowDots;
            return this;
        }

        public Builder setStrictNullHandling(boolean strictNullHandling) {
            this.strictNullHandling = strictNullHandling;
            return this;
        }

        public Options build() {
            return new Options(allowDots, strictNullHandling);
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
