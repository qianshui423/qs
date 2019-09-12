package com.qs.core.model;

public class StringifyOptions extends Options {

    // URI编码
    public static final boolean ENCODE = true;
    // 是否仅对value进行URI编码
    public static final boolean ENCODE_VALUES_ONLY = false;
    // 数组的表示形式
    public static final ArrayFormat ARRAY_FORMAT = ArrayFormat.INDICES;
    // 是否添加 ? 前缀
    public static final boolean ADD_QUERY_PREFIX = false;
    // 是否忽略null
    public static final boolean SKIP_NULLS = false;

    private boolean encode;
    private boolean encodeValuesOnly;
    private ArrayFormat arrayFormat;
    private boolean addQueryPrefix;
    private boolean skipNulls;

    private StringifyOptions(boolean allowDots, boolean strictNullHandling,
                             boolean encode, boolean encodeValuesOnly, ArrayFormat arrayFormat, boolean addQueryPrefix, boolean skipNulls) {
        super(allowDots, strictNullHandling);
        this.encode = encode;
        this.encodeValuesOnly = encodeValuesOnly;
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

    public ArrayFormat getArrayFormat() {
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
        private ArrayFormat arrayFormat = ARRAY_FORMAT;
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

        public Builder setArrayFormat(ArrayFormat arrayFormat) {
            if (arrayFormat == null) {
                this.arrayFormat = ArrayFormat.INDICES;
            } else {
                this.arrayFormat = arrayFormat;
            }
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
                    encode, encodeValuesOnly, arrayFormat, addQueryPrefix, skipNulls);
        }
    }
}
