package com.qs.core.parser;

import com.qs.core.log.Logger;
import com.qs.core.model.ArrayFormat;
import com.qs.core.model.ParseOptions;
import com.qs.core.model.QSArray;
import com.qs.core.model.QSObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class ParserHandler {

    /**
     * 处理解析 {@link ArrayFormat#BRACKETS} 格式数组
     */
    public static final int BRACKETS_NO_INDEX = -1000;

    private QSObject<String, Object> mQSObject = newObject();
    private LinkedList<Object> mPathQueue = new LinkedList<>();
    private QSArray<Object> mValueList = newArray();

    private ArrayFormat mArrayFormat = ArrayFormat.INDICES;

    private boolean mCollecting = false;

    void switchMode(ArrayFormat format) {
        mArrayFormat = format;
    }

    boolean isCommaMode() {
        return mArrayFormat == ArrayFormat.COMMA;
    }

    void pairKeyStart(ParseOptions options, QSToken token) {
        mCollecting = true;
        offerPath(token.value);
    }

    void pairValueEnd() {
        mCollecting = false;
        if (isCommaMode() && mValueList.size() == 1) {
            mPathQueue.pollLast();
        }
        put(mQSObject, mPathQueue, mValueList);
    }

    public QSObject<String, Object> getQSObject() {
        return mQSObject;
    }

    void reset() {
        mQSObject = newObject();
        mPathQueue = new LinkedList<>();
        mValueList = newArray();
    }

    public boolean isCollecting() {
        return mCollecting;
    }

    public void offerPath(Object path) {
        mPathQueue.offer(path);
    }

    public void offerValue(Object value) {
        mValueList.add(value);
    }

    private void put(QSObject<String, Object> qsObject, LinkedList<Object> pathQueue, QSArray<Object> valueList) {
        Object current = qsObject;
        Object child = null;
        int length = pathQueue.size();
        for (int i = 0; i < length - 1; i++) {
            Object path = pathQueue.get(i);
            if (isArrayIndex(path)) {
                int index = Integer.valueOf(String.valueOf(path));
                if (current instanceof QSArray) {
                    //noinspection unchecked
                    QSArray<Object> array = (QSArray<Object>) current;
                    if (isBracketsNoIndex(index) || index == array.size()) {
                        child = isArrayIndex(pathQueue.get(i + 1)) ? newArray() : newObject();
                        array.add(child);
                    } else if (index < array.size()) {
                        child = array.get(index);
                    } else {
                        String errorMsg = String.format(Locale.CHINA, "can't support skip add. please check path: %s", mPathQueue);
                        throw new IllegalArgumentException(errorMsg);
                    }
                } else {
                    String errorMsg = String.format(Locale.CHINA, "\"%d\" index conflicting, you wan't put array index into object key? please check path: %s", index, mPathQueue);
                    throw new IllegalArgumentException(errorMsg);
                }
            } else {
                String key = String.valueOf(path);
                if (current instanceof QSObject) {
                    //noinspection unchecked
                    QSObject<String, Object> object = (QSObject<String, Object>) current;
                    child = object.get(key);
                    if (child == null) child = isArrayIndex(pathQueue.get(i + 1)) ? newArray() : newObject();
                    object.put(key, child);
                } else {
                    String errorMsg = String.format(Locale.CHINA, "\"%s\" key conflicting, you wan't put object key into array index? please check path: %s", key, mPathQueue);
                    throw new IllegalArgumentException(errorMsg);
                }
            }
            current = child;
        }

        Object lastPath = pathQueue.peekLast();
        if (isArrayIndex(lastPath)) {
            int index = Integer.valueOf(String.valueOf(lastPath));
            if (current instanceof QSArray) {
                //noinspection unchecked
                QSArray<Object> array = (QSArray<Object>) current;
                if (isBracketsNoIndex(index) || index == array.size()) {
                    array.add(processValue(valueList));
                } else if (index < array.size()) {
                    if (array.get(index) != null) {
                        String warnMsg = String.format(Locale.CHINA, "\"%s\" key repeating, rewritten. please check path: %s", index, mPathQueue);
                        Logger.warn(warnMsg);
                    }
                    array.set(index, processValue(valueList));
                } else {
                    String errorMsg = String.format(Locale.CHINA, "can't support skip add. please check path: %s", mPathQueue);
                    throw new IllegalArgumentException(errorMsg);
                }
            } else {
                String errorMsg = String.format(Locale.CHINA, "\"%d\" index conflicting, you wan't put array index into object key? please check path: %s", index, mPathQueue);
                throw new IllegalArgumentException(errorMsg);
            }
        } else {
            String key = String.valueOf(lastPath);
            if (current instanceof QSObject) {
                //noinspection unchecked
                QSObject<String, Object> object = (QSObject<String, Object>) current;
                Object value = processValue(valueList);
                if (object.containsKey(key)) {
                    Object existObject = object.get(key);
                    if (existObject instanceof QSArray) {
                        //noinspection unchecked
                        QSArray<Object> existArray = ((QSArray<Object>) existObject);
                        if (value instanceof QSArray) {
                            //noinspection unchecked
                            existArray.addAll((QSArray<Object>) value);
                        } else {
                            existArray.add(value);
                        }
                    } else {
                        QSArray<Object> array = newArray();
                        array.add(object.get(key));
                        array.add(value);
                        object.put(key, array);
                    }
                } else {
                    object.put(key, value);
                }
            } else {
                String errorMsg = String.format(Locale.CHINA, "\"%s\" key conflicting, you wan't put object key into array index? please check path: %s", key, mPathQueue);
                throw new IllegalArgumentException(errorMsg);
            }
        }
        mPathQueue = new LinkedList<>();
        mValueList = newArray();
    }

    private Object processValue(List<Object> valueList) {
        if (valueList == null || valueList.isEmpty()) return null;
        if (valueList.size() == 1) return mValueList.get(0);
        return mValueList;
    }

    private QSArray<Object> newArray() {
        return new QSArray<>();
    }

    private QSObject<String, Object> newObject() {
        return new QSObject<>();
    }

    private boolean isArrayIndex(Object value) {
        return isNaturalNumber(value) || isBracketsNoIndex(value);
    }

    private boolean isNaturalNumber(Object value) {
        try {
            int number = Integer.valueOf(String.valueOf(value));
            return number >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isBracketsNoIndex(Object value) {
        try {
            int number = Integer.valueOf(String.valueOf(value));
            return number == BRACKETS_NO_INDEX;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
