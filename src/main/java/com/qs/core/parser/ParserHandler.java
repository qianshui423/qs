package com.qs.core.parser;

import com.qs.core.model.ArrayFormat;
import com.qs.core.model.ParseOptions;
import com.qs.core.model.QSArray;
import com.qs.core.model.QSObject;
import com.qs.core.util.NumberUtil;
import com.qs.core.util.QSDecoder;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ParserHandler {

    /**
     * 处理解析 {@link ArrayFormat#BRACKETS} 格式数组
     */
    private static final String BRACKETS_EMPTY_INDEX = "";
    private static final String CHAR_DOT = ".";
    private static final String REGEX_FIRST_DOT = "^\\.+";
    private static final String REGEX_DOT = "\\.+";
    private static final String CHAR_COMMA = ",";
    private static final String REGEX_COMMA = ",";
    private static final String WRAP_DEFAULT_PATH = "0";

    private QSObject mQSObject = newObject();
    private LinkedList<String> mPathQueue = new LinkedList<>();
    private QSArray mValueList = newArray();

    private ParseOptions mOptions;

    private int mParameterCount = 0;

    public ParserHandler(ParseOptions mOptions) {
        this.mOptions = mOptions;
    }

    void pairKeyStart(int position, QSToken token) throws ParseException {
        String decodePath = QSDecoder.decode(token.value);
        List<String> pathArray = PathParser.parse(decodePath, position);
        for (int i = 0; i < pathArray.size(); i++) {
            offerPath(pathArray.get(i));
        }
        mParameterCount++;
    }

    void pairValueEnd(int position) throws ParseException {
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

    public void offerPath(String path) {
        if (mOptions.isAllowDots() && path.length() > 1) { // 不允许 . 分割或者 path 为 1 时，则直接加入到 queue 中
            int indexDot = path.indexOf(CHAR_DOT);
            if (indexDot == -1) {
                mPathQueue.offer(path);
            } else {
                path = path.replaceAll(REGEX_FIRST_DOT, "");
                String[] pathArray = path.split(REGEX_DOT);
                mPathQueue.addAll(Arrays.asList(pathArray));
            }
        } else {
            mPathQueue.offer(path);
        }
    }

    public void offerValue(String value) {
        String decodeValue = QSDecoder.decode(value);
        if (mOptions.isComma() && !decodeValue.isEmpty()) {
            int indexComma = decodeValue.indexOf(CHAR_COMMA);
            if (indexComma == -1) {
                mValueList.add(decodeValue);
            } else {
                String[] valueArray = decodeValue.split(REGEX_COMMA, -1);
                mValueList.addAll(Arrays.asList(valueArray));
            }
        } else {
            mValueList.add(decodeValue);
        }
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
                String wrapPath = wrapPathValue(String.valueOf(path));
                child = object.get(wrapPath);
                if (child == null) child = isArrayIndex(pathQueue.get(i + 1)) ? newArray() : newObject();
                object.put(wrapPath, child);
            } else {
                if (isArrayIndex(path)) {
                    QSArray array = (QSArray) current;
                    if (isBracketsEmptyIndex(path)) {
                        child = isArrayIndex(pathQueue.get(i + 1)) ? newArray() : newObject();
                        array.add(child);
                    } else {
                        int pathIndex = Integer.valueOf(path);
                        if (pathIndex == array.size()) {
                            child = isArrayIndex(pathQueue.get(i + 1)) ? newArray() : newObject();
                            array.add(child);
                        } else if (pathIndex < array.size()) {
                            child = array.get(pathIndex);
                        } else {
                            throw new ParseException(position, ParseException.ERROR_SKIP_ADD_EXCEPTION, mPathQueue);
                        }
                    }
                } else {
                    QSObject convertObject = arrayToMap(current);
                    String wrapPath = wrapPathValue(String.valueOf(path));
                    child = isArrayIndex(pathQueue.get(i + 1)) ? newArray() : newObject();
                    convertObject.put(wrapPath, child);
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
            String wrapPath = wrapPathValue(String.valueOf(lastPath));
            Object value = processValue(valueList);
            if (object.containsKey(wrapPath)) {
                Object existObject = object.get(wrapPath);
                if (existObject instanceof QSArray) {
                    QSArray existArray = ((QSArray) existObject);
                    if (value instanceof QSArray) {
                        existArray.addAll((QSArray) value);
                    } else {
                        existArray.add(value);
                    }
                } else {
                    QSArray array = newArray();
                    array.add(object.get(wrapPath));
                    array.add(value);
                    object.put(wrapPath, array);
                }
            } else {
                object.put(wrapPath, value);
            }
        } else {
            if (isArrayIndex(lastPath)) {
                QSArray array = (QSArray) current;
                Object value = processValue(valueList);
                if (isBracketsEmptyIndex(lastPath)) {
                    array.add(value);
                } else {
                    int pathIndex = Integer.valueOf(lastPath);
                    if (pathIndex == array.size()) {
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
                }
            } else {
                Object value = processValue(valueList);
                String wrapPath = wrapPathValue(String.valueOf(lastPath));
                if (current instanceof QSArray) {
                    QSObject convertObject = arrayToMap(current);
                    convertObject.put(wrapPath, value);
                    connectToParent(parent, parentPath, convertObject);
                } else {
                    QSArray newArray = newArray();
                    newArray.add(current);
                    QSObject newObject = newObject();
                    newObject.put(wrapPath, value);
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

    private String wrapPathValue(String value) {
        if (isBracketsEmptyIndex(value) && !mOptions.isParseArrays()) {
            return WRAP_DEFAULT_PATH;
        }
        return value;
    }

    private QSArray newArray() {
        return new QSArray();
    }

    private QSObject newObject() {
        return new QSObject();
    }

    private boolean isArrayIndex(String value) {
        return (NumberUtil.isNaturalNumber(value) || isBracketsEmptyIndex(value)) && mOptions.isParseArrays();
    }

    private boolean isBracketsEmptyIndex(String value) {
        return BRACKETS_EMPTY_INDEX.equals(value);
    }
}
