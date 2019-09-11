package com.qs.core.stringify;

import com.qs.core.model.QSArray;
import com.qs.core.model.QSObject;
import com.qs.core.model.StringifyOptions;
import com.qs.core.uri.QSEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Stringifier {

    public static String toQString(QSObject object, StringifyOptions options) {
        return toQString(object, new ArrayList<>(), options);
    }

    private static String toQString(QSObject object, List<String> pathStack, StringifyOptions options) {
        if (pathStack == null) pathStack = new ArrayList<>();
        StringBuilder sb = new StringBuilder(33);
        for (Map.Entry<String, Object> entry : object.entrySet()) {
            pathStack.add(entry.getKey());
            toQString(entry.getValue(), sb, pathStack, options);
        }
        if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private static String toQString(QSArray array, List<String> pathStack, StringifyOptions options) {
        if (pathStack == null) pathStack = new ArrayList<>();
        StringBuilder sb = new StringBuilder(33);
        for (int i = 0, size = array.size(); i < size; ++i) {
            pathStack.add(String.valueOf(i));
            toQString(array.get(i), sb, pathStack, options);
        }
        if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private static void toQString(Object value, StringBuilder sb, List<String> pathStack, StringifyOptions options) {
        if (value instanceof QSArray) {
            sb.append(toQString((QSArray) value, pathStack, options));
        } else if (value instanceof QSObject) {
            sb.append(toQString((QSObject) value, pathStack, options));
        } else {
            sb.append(toPathString(pathStack, options));
            if (value != null) {
                sb.append('=');
                if (options.isEncode() || options.isEncodeValuesOnly()) {
                    sb.append(QSEncoder.encode(String.valueOf(value)));
                } else {
                    sb.append(value);
                }
            } else {
                if (!options.isStrictNullHandling()) {
                    sb.append('=');
                }
            }
        }
        pathStack.remove(pathStack.size() - 1);
        sb.append(options.getDelimiter());
    }

    private static String toPathString(List<String> pathStack, StringifyOptions options) {
        StringBuilder sb = new StringBuilder(33);
        for (int i = 0, size = pathStack.size(); i < size; ++i) {
            if (i == 0) {
                sb.append(pathStack.get(i));
            } else {
                sb.append('[').append(pathStack.get(i)).append(']');
            }
        }
        if (options.isEncode()) {
            return QSEncoder.encode(sb.toString());
        }
        return sb.toString();
    }

    public static String toJsonString(QSObject object) {
        StringBuilder sb = new StringBuilder(33);
        sb.append("{");
        for (Map.Entry<String, Object> entry : object.entrySet()) {
            sb.append('"').append(entry.getKey()).append("\":");
            toJsonString(entry.getValue(), sb);
        }
        if (sb.length() > 1) sb.deleteCharAt(sb.length() - 1);
        sb.append("}");
        return sb.toString();
    }

    private static String toJsonString(QSArray array) {
        StringBuilder sb = new StringBuilder(33);
        sb.append("[");
        for (Object value : array) {
            toJsonString(value, sb);
        }
        if (sb.length() > 1) sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        return sb.toString();
    }

    private static void toJsonString(Object value, StringBuilder sb) {
        if (value instanceof String) {
            sb.append('"').append((String) value).append('"');
        } else if (value instanceof QSArray) {
            sb.append(toJsonString((QSArray) value));
        } else if (value instanceof QSObject) {
            sb.append(toJsonString((QSObject) value));
        } else if (value instanceof Number || value instanceof Boolean) {
            sb.append(value.toString());
        } else if (value == null) {
            sb.append("null");
        } else {
            sb.append('"').append(value.toString()).append('"');
        }
        sb.append(',');
    }
}
