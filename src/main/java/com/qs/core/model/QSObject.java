package com.qs.core.model;

import com.qs.core.formatter.QSFormatter;
import com.qs.core.interfaces.QSAware;
import com.qs.core.interfaces.QSStreamAware;
import com.qs.core.stringify.Stringify;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;

public class QSObject extends LinkedHashMap<String, Object> implements QSAware, QSStreamAware {

    public QSObject() {
        super();
    }

    public QSObject(Map<String, Object> map) {
        super(map);
    }

    @Override
    public String toQString() {
        return Stringify.toQString(this);
    }

    @Override
    public String toString() {
        return QSFormatter.format(this);
    }

    @Override
    public void writeQSString(Writer out) throws IOException {

    }
}
