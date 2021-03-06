package com.qs.core.model;

import com.qs.core.formatter.QSFormatter;
import com.qs.core.interfaces.QSAware;
import com.qs.core.interfaces.QStreamAware;
import com.qs.core.stringify.Stringifier;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;

public class QSObject extends LinkedHashMap<String, Object> implements QSAware, QStreamAware {

    public QSObject() {
        super();
    }

    public QSObject(Map<String, String> map) {
        super(map);
    }

    @Override
    public String toQString() {
        return toQString(new StringifyOptions.Builder().build());
    }

    @Override
    public String toQString(StringifyOptions options) {
        return Stringifier.toQString(this, options);
    }

    @Override
    public void writeQString(Writer out) throws IOException {
        writeQString(out, new StringifyOptions.Builder().build());
    }

    @Override
    public void writeQString(Writer out, StringifyOptions options) throws IOException {
        out.write(toQString(options));
    }

    @Override
    public String toJsonString() {
        return Stringifier.toJsonString(this);
    }

    public String toFormatString() {
        return QSFormatter.format(this);
    }
}
