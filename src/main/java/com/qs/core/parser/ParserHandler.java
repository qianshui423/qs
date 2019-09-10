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
    public static final String BRACKETS_EMPTY_INDEX = "-1";

    private QSObject mQSObject = newObject();
    private LinkedList<String> mPathQueue = new LinkedList<>();
    private QSArray mValueList = newArray();

    private ArrayFormat mArrayFormat = ArrayFormat.INDICES;

    private ParseOptions mOptions;

    private int mParameterCount = 0;

    public ParserHandler(ParseOptions mOptions) {
        this.mOptions = mOptions;
    }

    void switchMode(ArrayFormat format) {
        mArrayFormat = format;
    }

    boolean isCommaMode() {
        return mArrayFormat == ArrayFormat.COMMA;
    }

    void pairKeyStart(QSToken token) {
        offerPath(token.value);
        mParameterCount++;
    }

    void pairValueEnd(int position) throws ParseException {
        if (isCommaMode() && mValueList.size() == 1) {
            mPathQueue.pollLast();
        }
        handleDepth();
        put(position, mQSObject, mPathQueue, mValueList);
    }

    boolean isUpperLimit() {
        return mParameterCount >= mOptions.getParameterLimit();
    }

    private void handleDepth() {
        int pathSize = mPathQueue.size();
        int optionDepth = mOptions.getDepth();
        int pathChildDepth = pathSize == 0 ? 0 : pathSize - 1;
        int dValue = pathChildDepth - optionDepth;
        if (dValue > 0) {
            StringBuilder mergePath = new StringBuilder();
            for (int i = 0; i < dValue; i++) {
                Object value = mPathQueue.remove(pathSize - dValue);
                mergePath.append("[").append(value).append("]");
            }
            mPathQueue.offer(mergePath.toString());
        }
    }

    public QSObject getQSObject() {
        return mQSObject;
    }

    void reset() {
        mQSObject = newObject();
        mPathQueue = new LinkedList<>();
        mValueList = newArray();
        mParameterCount = 0;
    }

    public void offerPath(String path) {
        mPathQueue.offer(path);
    }

    public void appendLastPath(String appendPath) {
        String lastPath = mPathQueue.pollLast();
        mPathQueue.offer((lastPath == null ? "" : lastPath) + appendPath);
    }

    public void offerValue(Object value) {
        mValueList.add(value);
    }

    private void put(int position, @Nonnull QSObject qsObject, LinkedList<String> pathQueue, QSArray valueList) throws ParseException {
        Object parent = null; // current 对象在父节点
        Object parentPath = null; // current 对象在父节点中的 key
        Object current = qsObject;
        Object child;
        int length = pathQueue.size();
        for (int i = 0; i < length - 1; i++) {
            String path = pathQueue.get(i);
            if (current instanceof QSObject) {
                QSObject object = (QSObject) current;
                String pathString = String.valueOf(path);
                child = object.get(pathString);
                if (child == null) child = isArrayIndex(pathQueue.get(i + 1)) ? newArray() : newObject();
                object.put(pathString, child);
            } else {
                if (isArrayIndex(path)) {
                    QSArray array = (QSArray) current;
                    int pathIndex = Integer.valueOf(path);
                    if (isBracketsEmptyIndex(path) || pathIndex == array.size()) {
                        child = isArrayIndex(pathQueue.get(i + 1)) ? newArray() : newObject();
                        array.add(child);
                    } else if (pathIndex < array.size()) {
                        child = array.get(pathIndex);
                    } else {
                        throw new ParseException(position, ParseException.ERROR_SKIP_ADD_EXCEPTION, mPathQueue);
                    }
                } else {
                    QSObject convertObject = arrayToMap(current);
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

        String lastPath = pathQueue.peekLast();
        if (current instanceof QSObject) {
            QSObject object = (QSObject) current;
            String pathString = String.valueOf(lastPath);
            Object value = processValue(valueList);
            if (object.containsKey(pathString)) {
                Object existObject = object.get(pathString);
                if (existObject instanceof QSArray) {
                    QSArray existArray = ((QSArray) existObject);
                    if (value instanceof QSArray) {
                        existArray.addAll((QSArray) value);
                    } else {
                        existArray.add(value);
                    }
                } else {
                    QSArray array = newArray();
                    array.add(object.get(pathString));
                    array.add(value);
                    object.put(pathString, array);
                }
            } else {
                object.put(pathString, value);
            }
        } else {
            if (isArrayIndex(lastPath)) {
                QSArray array = (QSArray) current;
                int pathIndex = Integer.valueOf(lastPath);
                Object value = processValue(valueList);
                if (isBracketsEmptyIndex(lastPath) || pathIndex == array.size()) {
                    array.add(value);
                } else if (pathIndex < array.size()) {
                    Object existObject = array.get(pathIndex);
                    if (existObject instanceof QSArray) {
                        QSArray existArray = ((QSArray) existObject);
                        existArray.add(value);
                    } else {
                        QSArray childArray = newArray();
                        childArray.add(existObject);
                        childArray.add(value);
                        array.set(pathIndex, childArray);
                    }
                } else {
                    throw new ParseException(position, ParseException.ERROR_SKIP_ADD_EXCEPTION, mPathQueue);
                }
            } else {
                Object value = processValue(valueList);
                if (current instanceof QSArray) {
                    QSObject convertObject = arrayToMap(current);
                    convertObject.put(String.valueOf(lastPath), value);
                    connectToParent(parent, parentPath, convertObject);
                } else {
                    QSArray newArray = newArray();
                    newArray.add(current);
                    QSObject newObject = newObject();
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
            QSObject parentObject = (QSObject) parent;
            parentObject.put(String.valueOf(parentPath), linkObject);
        } else {
            QSArray parentArray = (QSArray) parent;
            parentArray.set(Integer.valueOf(String.valueOf(parentPath)), linkObject);
        }
    }

    private QSObject arrayToMap(Object array) {
        QSArray qsArray = (QSArray) array;
        final int size = qsArray.size();
        final QSObject qsObject = newObject();
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

    private QSArray newArray() {
        return new QSArray();
    }

    private QSObject newObject() {
        return new QSObject();
    }

    private boolean isArrayIndex(String value) {
        return isNaturalNumber(value) || isBracketsEmptyIndex(value);
    }

    private boolean isNaturalNumber(String value) {
        try {
            int number = Integer.valueOf(value);
            return number >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isBracketsEmptyIndex(String value) {
        try {
            int number = Integer.valueOf(value);
            return number == Integer.valueOf(BRACKETS_EMPTY_INDEX);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
