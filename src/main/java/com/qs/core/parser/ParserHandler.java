package com.qs.core.parser;

import com.qs.core.model.ArrayFormat;
import com.qs.core.model.ParseOptions;
import com.qs.core.model.QSArray;
import com.qs.core.model.QSObject;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

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

    @SuppressWarnings("ConstantConditions")
    private void put(@Nonnull QSObject<String, Object> qsObject, LinkedList<Object> pathQueue, QSArray<Object> valueList) {
        Object parent = null; // current 对象在父节点
        Object parentPath = null; // current 对象在父节点中的 key
        Object current = qsObject;
        Object child = null;
        int length = pathQueue.size();
        for (int i = 0; i < length - 1; i++) {
            Object path = pathQueue.get(i);
            if (current instanceof QSObject) {
                //noinspection unchecked
                QSObject<String, Object> object = (QSObject<String, Object>) current;
                String pathString = String.valueOf(path);
                child = object.get(pathString);
                if (child == null) child = isArrayIndex(pathQueue.get(i + 1)) ? newArray() : newObject();
                object.put(pathString, child);
            } else {
                if (isArrayIndex(path)) {
                    //noinspection unchecked
                    QSArray<Object> array = (QSArray<Object>) current;
                    int pathIndex = Integer.valueOf(String.valueOf(path));
                    if (pathIndex >= 0 && pathIndex < array.size()) {
                        child = array.get(pathIndex);
                    } else {
                        child = isArrayIndex(pathQueue.get(i + 1)) ? newArray() : newObject();
                        array.add(child);
                    }
                } else {
                    QSObject<String, Object> convertObject = arrayToMap(current);
                    String pathString = String.valueOf(path);
                    child = isArrayIndex(pathQueue.get(i + 1)) ? newArray() : newObject();
                    convertObject.put(pathString, child);
                    connectToParent(parent, parentPath, convertObject);
                }
            }
            parentPath = path;
            parent = current;
            current = child;
        }

        Object lastPath = pathQueue.peekLast();
        if (current instanceof QSObject) {
            //noinspection unchecked
            QSObject<String, Object> object = (QSObject<String, Object>) current;
            String pathString = String.valueOf(lastPath);
            Object value = processValue(valueList);
            if (object.containsKey(pathString)) {
                Object existObject = object.get(pathString);
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
                    array.add(object.get(pathString));
                    array.add(value);
                    object.put(pathString, array);
                }
            } else {
                object.put(pathString, value);
            }
        } else {
            if (isArrayIndex(lastPath)) {
                //noinspection unchecked
                QSArray<Object> array = (QSArray<Object>) current;
                int pathIndex = Integer.valueOf(String.valueOf(lastPath));
                Object value = processValue(valueList);
                if (pathIndex >= 0 && pathIndex < array.size()) {
                    Object existObject = array.get(pathIndex);
                    if (existObject instanceof QSArray) {
                        //noinspection unchecked
                        QSArray<Object> existArray = ((QSArray<Object>) existObject);
                        existArray.add(value);
                    } else {
                        QSArray<Object> childArray = newArray();
                        childArray.add(existObject);
                        childArray.add(value);
                        array.set(pathIndex, childArray);
                    }
                } else {
                    array.add(value);
                }
            } else {
                Object value = processValue(valueList);
                if (current instanceof QSArray) {
                    QSObject<String, Object> convertObject = arrayToMap(current);
                    convertObject.put(String.valueOf(lastPath), value);
                    connectToParent(parent, parentPath, convertObject);
                } else {
                    QSArray<Object> newArray = newArray();
                    newArray.add(current);
                    QSObject<String, Object> newObject = newObject();
                    newObject.put(String.valueOf(lastPath), value);
                    newArray.add(newObject);
                    connectToParent(parent, parentPath, newArray);
                }
            }
        }
        mPathQueue = new LinkedList<>();
        mValueList = newArray();
    }

    private void connectToParent(Object parent, Object parentPath, Object linkObject) {
        if (parent instanceof QSObject) {
            //noinspection unchecked
            QSObject<String, Object> parentObject = (QSObject<String, Object>) parent;
            parentObject.put(String.valueOf(parentPath), linkObject);
        } else {
            //noinspection unchecked
            QSArray<Object> parentArray = (QSArray<Object>) parent;
            parentArray.set(Integer.valueOf(String.valueOf(parentPath)), linkObject);
        }
    }

    private QSObject<String, Object> arrayToMap(Object array) {
        //noinspection unchecked
        QSArray<Object> qsArray = (QSArray<Object>) array;
        final int size = qsArray.size();
        final QSObject<String, Object> qsObject = newObject();
        for (int i = 0; i < size; ++i) {
            qsObject.put(String.valueOf(i), qsArray.get(i));
        }
        return qsObject;
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
