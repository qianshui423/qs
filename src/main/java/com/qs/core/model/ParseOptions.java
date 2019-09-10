package com.qs.core.model;

public class ParseOptions extends Options {

    // 最大解析深度
    public static final int DEPTH = 5;
    // 最大参数数量
    public static final int PARAMETER_LIMIT = 1000;
    // 是否忽略&前缀
    public static final boolean IGNORE_QUERY_PREFIX = false;
    // 数组索引的最大值，超过则会解析成对象形式
    public static final int ARRAY_LIMIT = 20;
    // []是否以数组形式解析
    public static final boolean PARSE_ARRAYS = true;
    // 是否可解析出逗号分隔的数组元素
    public static final boolean COMMA = false;

    private int depth;
    private int parameterLimit;
    private boolean ignoreQueryPrefix;
    private int arrayLimit;
    private boolean parseArrays;
    private boolean comma;

    public ParseOptions(boolean allowDots, boolean strictNullHandling,
                        int depth, int parameterLimit, boolean ignoreQueryPrefix, int arrayLimit,
                        boolean parseArrays, boolean comma) {
        super(allowDots, strictNullHandling);
        this.depth = depth;
        this.parameterLimit = parameterLimit;
        this.ignoreQueryPrefix = ignoreQueryPrefix;
        this.arrayLimit = arrayLimit;
        this.parseArrays = parseArrays;
        this.comma = comma;
    }

    public int getDepth() {
        return depth;
    }

    public int getParameterLimit() {
        return parameterLimit;
    }

    public boolean isIgnoreQueryPrefix() {
        return ignoreQueryPrefix;
    }

    public int getArrayLimit() {
        return arrayLimit;
    }

    public boolean isParseArrays() {
        return parseArrays;
    }

    public boolean isComma() {
        return comma;
    }

    public static class Builder extends Options.Builder {
        private int depth = DEPTH;
        private int parameterLimit = PARAMETER_LIMIT;
        private boolean ignoreQueryPrefix = IGNORE_QUERY_PREFIX;
        private int arrayLimit = ARRAY_LIMIT;
        private boolean parseArrays = PARSE_ARRAYS;
        private boolean comma = COMMA;

        public Builder setDepth(int depth) {
            this.depth = depth;
            return this;
        }

        public Builder setParameterLimit(int parameterLimit) {
            this.parameterLimit = parameterLimit;
            return this;
        }

        public Builder setIgnoreQueryPrefix(boolean ignoreQueryPrefix) {
            this.ignoreQueryPrefix = ignoreQueryPrefix;
            return this;
        }

        public Builder setArrayLimit(int arrayLimit) {
            this.arrayLimit = arrayLimit;
            return this;
        }

        public Builder setParseArrays(boolean parseArrays) {
            this.parseArrays = parseArrays;
            return this;
        }

        public Builder setComma(boolean comma) {
            this.comma = comma;
            return this;
        }

        public Builder setAllowDots(boolean allowDots) {
            super.setAllowDots(allowDots);
            return this;
        }

        public Builder setStrictNullHandling(boolean strictNullHandling) {
            super.setStrictNullHandling(strictNullHandling);
            return this;
        }

        public ParseOptions build() {
            Options options = super.build();
            return new ParseOptions(options.isAllowDots(), options.isStrictNullHandling(),
                    depth, parameterLimit, ignoreQueryPrefix, arrayLimit, parseArrays, comma);
        }
    }
}
