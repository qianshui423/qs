package com.qs.core.interfaces;

import java.io.IOException;
import java.io.Writer;

public interface QStreamAware {
    void writeQString(Writer out) throws IOException;
}
