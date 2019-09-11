package com.qs.core.model;

public class StringifyOptions extends Options {

    // URI编码
    public static final boolean ENCODE = true;
    // 是否仅对value进行URI编码
    public static final boolean ENCODE_VALUES_ONLY = false;
    // 是否省略数组的[]，采用 repeat 模式表示数组
    public static final boolean INDICES = true;
    // 数组的表示形式
    public static final String ARRAY_FORMAT = ArrayFormat.INDICES.getCode();
    // 是否添加 ? 前缀
    public static final boolean ADD_QUERY_PREFIX = false;
    // 是否忽略null
    public static final boolean SKIP_NULLS = false;

    private boolean encode;
    private boolean encodeValuesOnly;
    private boolean indices;
    private String arrayFormat;
    private boolean addQueryPrefix;
    private boolean skipNulls;

    private StringifyOptions(boolean allowDots, boolean strictNullHandling,
                             boolean encode, boolean encodeValuesOnly, boolean indices,
                             String arrayFormat, boolean addQueryPrefix, boolean skipNulls) {
        super(allowDots, strictNullHandling);
        this.encode = encode;
        this.encodeValuesOnly = encodeValuesOnly;
        this.indices = indices;
        this.arrayFormat = arrayFormat;
        this.addQueryPrefix = addQueryPrefix;
        this.skipNulls = skipNulls;
    }

    public boolean isEncode() {
        return encode;
    }

    public boolean isEncodeValuesOnly() {
        return encodeValuesOnly;
    }

    public boolean isIndices() {
        return indices;
    }

    public String getArrayFormat() {
        return arrayFormat;
    }

    public boolean isAddQueryPrefix() {
        return addQueryPrefix;
    }

    public boolean isSkipNulls() {
        return skipNulls;
    }

    public static class Builder extends Options.Builder {
        private boolean encode = ENCODE;
        private boolean encodeValuesOnly = ENCODE_VALUES_ONLY;
        private boolean indices = INDICES;
        private String arrayFormat = ARRAY_FORMAT;
        private boolean addQueryPrefix = ADD_QUERY_PREFIX;
        private boolean skipNulls = SKIP_NULLS;

        public Builder setEncode(boolean encode) {
            this.encode = encode;
            return this;
        }

        public Builder setEncodeValuesOnly(boolean encodeValuesOnly) {
            this.encodeValuesOnly = encodeValuesOnly;
            return this;
        }

        public Builder setIndices(boolean indices) {
            this.indices = indices;
            return this;
        }

        public Builder setArrayFormat(String arrayFormat) {
            this.arrayFormat = arrayFormat;
            return this;
        }

        public Builder setAddQueryPrefix(boolean addQueryPrefix) {
            this.addQueryPrefix = addQueryPrefix;
            return this;
        }

        public Builder setSkipNulls(boolean skipNulls) {
            this.skipNulls = skipNulls;
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

        public StringifyOptions build() {
            Options options = super.build();
            return new StringifyOptions(options.isAllowDots(), options.isStrictNullHandling(),
                    encode, encodeValuesOnly, indices, arrayFormat, addQueryPrefix, skipNulls);
        }
    }
}
