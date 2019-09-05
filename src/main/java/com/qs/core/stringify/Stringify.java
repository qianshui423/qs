package com.qs.core.stringify;

import com.qs.core.model.QSArray;
import com.qs.core.model.QSObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Stringify {

    public static String toQString(QSObject object) {
        return toQString(object, null);
    }

    private static String toQString(QSObject object, List<String> pathStack) {
        if (pathStack == null) pathStack = new ArrayList<>();
        StringBuilder sb = new StringBuilder(33);
        for (Map.Entry<String, Object> entry : object.entrySet()) {
            pathStack.add(entry.getKey());
            toQString(entry.getValue(), sb, pathStack);
        }
        if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private static String toQString(QSArray array, List<String> pathStack) {
        if (pathStack == null) pathStack = new ArrayList<>();
        StringBuilder sb = new StringBuilder(33);
        for (int i = 0, size = array.size(); i < size; ++i) {
            pathStack.add(String.valueOf(i));
            toQString(array.get(i), sb, pathStack);
        }
        if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private static void toQString(Object value, StringBuilder sb, List<String> pathStack) {
        if (value instanceof QSArray) {
            sb.append(toQString((QSArray) value, pathStack));
        } else if (value instanceof QSObject) {
            sb.append(toQString((QSObject) value, pathStack));
        } else {
            sb.append(toPathString(pathStack)).append('=').append(value);
        }
        pathStack.remove(pathStack.size() - 1);
        sb.append('&');
    }

    private static String toPathString(List<String> pathStack) {
        StringBuilder sb = new StringBuilder(33);
        for (int i = 0, size = pathStack.size(); i < size; ++i) {
            if (i == 0) {
                sb.append(pathStack.get(i));
            } else {
                sb.append('[').append(pathStack.get(i)).append(']');
            }
        }
        return sb.toString();
    }
}
