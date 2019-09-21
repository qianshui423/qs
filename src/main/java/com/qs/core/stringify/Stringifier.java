package com.qs.core.stringify;

import com.qs.core.model.ArrayFormat;
import com.qs.core.model.QSArray;
import com.qs.core.model.QSObject;
import com.qs.core.model.StringifyOptions;
import com.qs.core.util.QSEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Stringifier {

    private static final String QUERY_PREFIX = "?";
    private static final char LEFT_SQUARE = '[';
    private static final char RIGHT_SQUARE = ']';
    private static final char DOT = '.';

    public static String toQString(QSObject object) {
        return toQString(object, new StringifyOptions.Builder().build());
    }

    public static String toQString(QSObject object, StringifyOptions options) {
        StringBuilder sb = new StringBuilder();
        if (options.isAddQueryPrefix()) {
            sb.append(QUERY_PREFIX);
        }
        sb.append(toQString(object, new ArrayList<>(), options));
        return sb.toString();
    }

    private static String toQString(QSObject object, List<Object> pathStack, StringifyOptions options) {
        if (pathStack == null) pathStack = new ArrayList<>();
        StringBuilder sb = new StringBuilder(33);
        for (Map.Entry<String, Object> entry : object.entrySet()) {
            pathStack.add(entry.getKey());
            toQString(entry.getValue(), sb, pathStack, options);
        }
        if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private static String toQString(QSArray array, List<Object> pathStack, StringifyOptions options) {
        if (pathStack == null) pathStack = new ArrayList<>();
        StringBuilder sb = new StringBuilder(33);
        for (int i = 0, size = array.size(); i < size; ++i) {
            pathStack.add(i);
            toQString(array.get(i), sb, pathStack, options);
        }
        if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private static void toQString(Object value, StringBuilder sb, List<Object> pathStack, StringifyOptions options) {
        if (value instanceof QSArray) {
            if (options.getArrayFormat() == ArrayFormat.COMMA) {
                sb.append(toCommaQString((QSArray) value, pathStack, options));
            } else {
                sb.append(toQString((QSArray) value, pathStack, options));
            }
        } else if (value instanceof QSObject) {
            sb.append(toQString((QSObject) value, pathStack, options));
        } else {
            if (value != null) {
                sb.append(toPathString(pathStack, options));
                sb.append('=');
                if (options.isEncode()) {
                    sb.append(QSEncoder.encode(String.valueOf(value)));
                } else {
                    sb.append(value);
                }
            } else {
                if (!options.isSkipNulls()) {
                    sb.append(toPathString(pathStack, options));
                    if (!options.isStrictNullHandling()) {
                        sb.append('=');
                    }
                }
            }
        }
        pathStack.remove(pathStack.size() - 1);
        if (value != null || !options.isSkipNulls()) {
            if (value instanceof QSArray && ((QSArray) value).isEmpty()) return;
            sb.append(options.getDelimiter());
        }
    }

    private static String toCommaQString(QSArray array, List<Object> pathStack, StringifyOptions options) {
        StringBuilder sb = new StringBuilder(33);
        sb.append(toPathString(pathStack, options)).append("=");
        for (int i = 0, size = array.size(); i < size; ++i) {
            Object value = array.get(i);
            if (value != null) {
                sb.append(value);
            }
            sb.append(",");
        }
        if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private static String toPathString(List<Object> pathStack, StringifyOptions options) {
        StringBuilder sb = new StringBuilder(33);
        int size = pathStack.size();
        for (int i = 0; i < size; ++i) {
            Object path = pathStack.get(i);
            if (options.isAllowDots()) {
                sb.append(path).append(DOT);
            } else {
                if (i == 0) {
                    sb.append(path);
                } else if (i == size - 1) { // 最后一个 path 的处理
                    ArrayFormat format = options.getArrayFormat();
                    if (format == ArrayFormat.INDICES) {
                        sb.append(LEFT_SQUARE).append(path).append(RIGHT_SQUARE);
                    } else if (format == ArrayFormat.BRACKETS) {
                        if (isIntegerType(path)) {
                            sb.append(LEFT_SQUARE).append(RIGHT_SQUARE);
                        } else {
                            sb.append(LEFT_SQUARE).append(path).append(RIGHT_SQUARE);
                        }
                    } else if (format == ArrayFormat.REPEAT) {
                        if (!isIntegerType(path)) {
                            sb.append(LEFT_SQUARE).append(path).append(RIGHT_SQUARE);
                        }
                    } else if (format == ArrayFormat.COMMA) {
                        sb.append(LEFT_SQUARE).append(path).append(RIGHT_SQUARE);
                    }
                } else {
                    ArrayFormat format = options.getArrayFormat();
                    if (format == ArrayFormat.BRACKETS && isIntegerType(path)) {
                        sb.append(LEFT_SQUARE).append(RIGHT_SQUARE);
                    } else {
                        sb.append(LEFT_SQUARE).append(path).append(RIGHT_SQUARE);
                    }
                }
            }
        }
        if (options.isAllowDots()) {
            sb.deleteCharAt(sb.length() - 1);
        }
        if (options.isEncode() && !options.isEncodeValuesOnly()) {
            return QSEncoder.encode(sb.toString());
        }
        return sb.toString();
    }

    private static boolean isIntegerType(Object value) {
        return value instanceof Integer;
    }


    public static String toJsonString(QSObject object) {
        StringBuilder sb = new StringBuilder(33);
        sb.append("{");
        for (Map.Entry<String, Object> entry : object.entrySet()) {
            sb.append("\"").append(entry.getKey()).append("\":");
            toJsonString(entry.getValue(), sb);
        }
        if (sb.length() > 1) sb.deleteCharAt(sb.length() - 1);
        sb.append("}");
        return sb.toString();
    }

    private static String toJsonString(QSArray array) {
        StringBuilder sb = new StringBuilder(33);
        sb.append(LEFT_SQUARE);
        for (Object value : array) {
            toJsonString(value, sb);
        }
        if (sb.length() > 1) sb.deleteCharAt(sb.length() - 1);
        sb.append(LEFT_SQUARE);
        return sb.toString();
    }

    private static void toJsonString(Object value, StringBuilder sb) {
        if (value instanceof String) {
            sb.append("\"").append((String) value).append("\"");
        } else if (value instanceof QSArray) {
            sb.append(toJsonString((QSArray) value));
        } else if (value instanceof QSObject) {
            sb.append(toJsonString((QSObject) value));
        } else if (value instanceof Number || value instanceof Boolean) {
            sb.append(value.toString());
        } else if (value == null) {
            sb.append("null");
        } else {
            sb.append("\"").append(value.toString()).append("\"");
        }
        sb.append(",");
    }
}
