package com.qs.core.interfaces;

import java.io.IOException;
import java.io.Writer;

public interface QSStreamAware {
    void writeQSString(Writer out) throws IOException;
}
