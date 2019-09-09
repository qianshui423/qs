package com.qs.core.interfaces;

import com.qs.core.model.StringifyOptions;

public interface QSAware {

    String toQString(StringifyOptions options);

    String toQString();

    String toJsonString();
}
