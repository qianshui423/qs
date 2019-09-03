package com.qs.core.model;

import com.qs.core.formatter.QSFormatter;
import com.qs.core.interfaces.QSAware;
import com.qs.core.interfaces.QSStreamAware;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;

public class QSObject<K, V> extends LinkedHashMap<K, V> implements QSAware, QSStreamAware {

    public QSObject() {
        super();
    }

    public QSObject(Map<K, V> map) {
        super(map);
    }

    @Override
    public String toQString() {
        return "";
    }

    @Override
    public String toString() {
        return QSFormatter.format(this);
    }

    @Override
    public void writeQSString(Writer out) throws IOException {

    }
}
