package com.qs.core.formatter;

import com.qs.core.model.QSArray;
import com.qs.core.model.QSObject;
import com.qs.core.model.ColorString;

import java.util.Map;

public class QSFormatter {

    private static int mLevel = 0;
    private static final String UNIT_TAB_SPACE = "\t";
    private static final String UNIT_SPACE = " ";

    public static String format(QSObject input) {
        StringBuilder builder = new StringBuilder(input.size() * 4);
        formatObject("", true, input, builder);
        return builder.toString();
    }

    private static <K, V> void formatObject(String inputKey, boolean parentLast, QSObject input, StringBuilder builder) {
        printKey(builder, inputKey);
        printBlockStartSign(builder, "{");
        int i = 0;
        int length = input.size();
        for (Map.Entry entity : input.entrySet()) {
            boolean last = i == length - 1;
            String key = entity.getKey().toString();
            Object value = entity.getValue();
            if (value instanceof QSObject) {
                mLevel++;
                formatObject(key, last, (QSObject) value, builder);
                mLevel--;
            } else if (value instanceof QSArray) {
                mLevel++;
                formatArray(key, last, (QSArray) value, builder);
                mLevel--;
            } else {
                mLevel++;
                printKey(builder, key);
                printValue(builder, value, last);
                mLevel--;
            }
            i++;
        }
        printBlockEndSign(builder, parentLast, "}");
    }

    private static <T> void formatArray(String inputKey, boolean parentLast, QSArray input, StringBuilder builder) {
        printKey(builder, inputKey);
        printBlockStartSign(builder, "[");
        int i = 0;
        int length = input.size();
        for (Object value : input) {
            boolean last = i == length - 1;
            if (value instanceof QSObject) {
                mLevel++;
                formatObject("", last, (QSObject) value, builder);
                mLevel--;
            } else if (value instanceof QSArray) {
                mLevel++;
                formatArray("", last, (QSArray) value, builder);
                mLevel--;
            } else {
                mLevel++;
                tabIndent(builder);
                printValue(builder, value, last);
                mLevel--;
            }
            i++;
        }
        printBlockEndSign(builder, parentLast, "]");
    }

    private static void printKey(StringBuilder builder, String key) {
        tabIndent(builder);
        builder.append(colorKey(key));
        if (!"".equals(key)) {
            builder.append(":");
        }
    }

    private static void printValue(StringBuilder builder, Object value, boolean last) {
        if (value == null) {
            builder.append(colorValue("null"));
        } else {
            builder.append(colorValue("\"" + value + "\""));
        }
        if (!last) {
            builder.append(",");
            builder.append("\n");
        }
    }

    private static void printBlockStartSign(StringBuilder builder, String startSign) {
        builder.append(startSign).append("\n");
    }

    private static void printBlockEndSign(StringBuilder builder, boolean last, String endSign) {
        builder.append("\n");
        tabIndent(builder);
        builder.append(endSign);
        if (!last) {
            builder.append(",");
            builder.append("\n");
        }
    }

    private static void tabIndent(StringBuilder builder) {
        for (int i = 0; i < mLevel; i++) {
            builder.append(UNIT_TAB_SPACE);
        }
    }

    private static void spaceIndent(StringBuilder builder) {
        builder.append(UNIT_SPACE);
    }

    private static String colorKey(String key) {
        return key;
        // return ColorString.GREEN.getCode() + key + ColorString.BLACK.getCode();
    }

    private static String colorValue(String value) {
        return ColorString.BLUE.getCode() + value + ColorString.BLACK.getCode();
    }
}
