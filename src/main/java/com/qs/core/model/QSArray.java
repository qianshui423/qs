package com.qs.core.model;

import com.qs.core.interfaces.QSAware;
import com.qs.core.interfaces.QSStreamAware;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;

public class QSArray<E> extends ArrayList<E> implements QSAware, QSStreamAware {

    public QSArray() {
        super();
    }

    public QSArray(Collection<? extends E> c) {
        super(c);
    }

    @Override
    public String toQString() {
        return "";
    }

    @Override
    public void writeQSString(Writer out) throws IOException {

    }
}
