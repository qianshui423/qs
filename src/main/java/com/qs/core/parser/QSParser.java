package com.qs.core.parser;

import com.qs.core.QSArray;
import com.qs.core.QSObject;
import com.qs.core.log.Logger;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.Locale;

public class QSParser {

    public static final int S_INIT = 0;
    public static final int S_IN_FINISHED_OBJECT_KEY = 1;
    public static final int S_IN_FINISHED_ARRAY_INDEX = 2;
    public static final int S_IN_FINISHED_LEFT_SQUARE = 4;
    public static final int S_IN_FINISHED_RIGHT_SQUARE = 5;
    public static final int S_IN_FINISHED_VALUE = 6;
    public static final int S_IN_FINISHED_EQUAL_SIGN = 7;
    public static final int S_IN_ERROR = -1;

    private QSLex mLexer = new QSLex(null);
    private QSToken mToken = null;
    private int mStatus = S_INIT;

    public void reset() {
        mToken = null;
        mStatus = S_INIT;
    }

    public void reset(Reader in) {
        mLexer.yyreset(in);
        reset();
    }

    public int getPosition() {
        return mLexer.getPosition();
    }

    public QSObject<String, Object> parse(String s) throws ParseException {
        StringReader in = new StringReader(s);
        try {
            return parse(in);
        } catch (IOException e) {
            throw new ParseException(S_IN_ERROR, ParseException.ERROR_UNEXPECTED_EXCEPTION, e);
        }
    }

    public QSObject<String, Object> parse(Reader in) throws IOException, ParseException {
        reset(in);
        LinkedList<Integer> statusQueue = new LinkedList<>();
        LinkedList<Object> valueQueue = new LinkedList<>();
        QSObject<String, Object> qsObject = new QSObject<>();
        do {
            nextToken();
            switch (mStatus) {
                case S_INIT: {
                    switch (mToken.type) {
                        case QSToken.TYPE_AND: {
                            break;
                        }
                        case QSToken.TYPE_VALUE: {
                            if (isNaturalNumber(mToken.value)) {
                                mStatus = S_IN_FINISHED_ARRAY_INDEX;
                            } else {
                                mStatus = S_IN_FINISHED_OBJECT_KEY;
                            }
                            statusQueue.addLast(mStatus);
                            valueQueue.addLast(mToken.value);
                            break;
                        }
                        default: {
                            mStatus = S_IN_ERROR;
                            break;
                        }
                    }
                    break;
                }
                case S_IN_FINISHED_OBJECT_KEY:
                case S_IN_FINISHED_ARRAY_INDEX: {
                    switch (mToken.type) {
                        case QSToken.TYPE_LEFT_SQUARE: {
                            mStatus = S_IN_FINISHED_LEFT_SQUARE;
                            break;
                        }
                        case QSToken.TYPE_RIGHT_SQUARE: {
                            mStatus = S_IN_FINISHED_RIGHT_SQUARE;
                            break;
                        }
                        default: {
                            mStatus = S_IN_ERROR;
                            break;
                        }
                    }
                    break;
                }
                case S_IN_FINISHED_LEFT_SQUARE: {
                    switch (mToken.type) {
                        case QSToken.TYPE_VALUE: {
                            if (isNaturalNumber(mToken.value)) {
                                mStatus = S_IN_FINISHED_ARRAY_INDEX;
                            } else {
                                mStatus = S_IN_FINISHED_OBJECT_KEY;
                            }
                            statusQueue.addLast(mStatus);
                            valueQueue.addLast(mToken.value);
                            break;
                        }
                        case QSToken.TYPE_RIGHT_SQUARE: {
                            mStatus = S_IN_FINISHED_RIGHT_SQUARE;
                            break;
                        }
                        default: {
                            mStatus = S_IN_ERROR;
                            break;
                        }
                    }
                    break;
                }
                case S_IN_FINISHED_RIGHT_SQUARE: {
                    switch (mToken.type) {
                        case QSToken.TYPE_LEFT_SQUARE: {
                            mStatus = S_IN_FINISHED_LEFT_SQUARE;
                            break;
                        }
                        case QSToken.TYPE_EQUAL_SIGN: {
                            mStatus = S_IN_FINISHED_EQUAL_SIGN;
                            break;
                        }
                        default: {
                            mStatus = S_IN_ERROR;
                            break;
                        }
                    }
                    break;
                }
                case S_IN_FINISHED_EQUAL_SIGN: {
                    switch (mToken.type) {
                        case QSToken.TYPE_VALUE: {
                            mStatus = S_IN_FINISHED_VALUE;
                            statusQueue.addLast(mStatus);
                            valueQueue.addLast(mToken.value);
                            break;
                        }
                        case QSToken.TYPE_AND:
                        case QSToken.TYPE_EOF: {
                            mStatus = S_INIT;
                            put(qsObject, statusQueue, valueQueue);
                            statusQueue = new LinkedList<>();
                            valueQueue = new LinkedList<>();
                            break;
                        }
                        default: {
                            mStatus = S_IN_ERROR;
                            break;
                        }
                    }
                    break;
                }
                case S_IN_FINISHED_VALUE: {
                    mStatus = S_INIT;
                    if (mToken.type == QSToken.TYPE_AND || mToken.type == QSToken.TYPE_EOF) {
                        put(qsObject, statusQueue, valueQueue);
                        statusQueue = new LinkedList<>();
                        valueQueue = new LinkedList<>();
                    }
                    break;
                }
                case S_IN_ERROR:
                    throw new ParseException(getPosition(), ParseException.ERROR_UNEXPECTED_TOKEN, mToken);
            }
            if (mStatus == S_IN_ERROR) {
                throw new ParseException(getPosition(), ParseException.ERROR_UNEXPECTED_TOKEN, mToken);
            }
        } while (mToken.type != QSToken.TYPE_EOF);

        return qsObject;
    }

    private QSArray<Object> newArray() {
        return new QSArray<>();
    }

    private QSObject<String, Object> newObject() {
        return new QSObject<>();
    }

    private void put(QSObject<String, Object> qsObject, LinkedList<Integer> statusQueue, LinkedList<Object> valueQueue) {
        QSObject<Object, Object> value = new QSObject<>();
        Object current = qsObject;
        Object child = null;

        Object pairValue = null;
        if (hasPairValue(statusQueue)) {
            statusQueue.removeLast();
            pairValue = valueQueue.removeLast();
        }
        int statusLength = statusQueue.size();
        for (int i = 0, size = statusLength - 1; i < size; i++) {
            int status = Integer.valueOf(statusQueue.get(i).toString());
            switch (status) {
                case S_IN_FINISHED_OBJECT_KEY: {
                    String key = valueQueue.get(i).toString();
                    if (current instanceof QSObject) {
                        //noinspection unchecked
                        QSObject<String, Object> object = (QSObject<String, Object>) current;
                        child = object.get(key);
                        if (child == null) child = isNaturalNumber(valueQueue.get(i + 1)) ? newArray() : newObject();
                        object.put(key, child);
                    } else {
                        String errorMsg = String.format(Locale.CHINA, "\"%s\" key conflicting, you wan't put object key into array index? please check path: %s", key, valueQueue);
                        throw new IllegalArgumentException(errorMsg);
                    }
                    break;
                }
                case S_IN_FINISHED_ARRAY_INDEX: {
                    int index = Integer.valueOf(valueQueue.get(i).toString());
                    if (current instanceof QSArray) {
                        //noinspection unchecked
                        QSArray<Object> array = (QSArray<Object>) current;
                        if (index < array.size()) {
                            child = array.get(index);
                        } else if (index == array.size()) {
                            child = isNaturalNumber(valueQueue.get(i + 1)) ? newArray() : newObject();
                            array.add(child);
                        } else {
                            String errorMsg = String.format(Locale.CHINA, "can't support skip add. please check path: %s", valueQueue);
                            throw new IllegalArgumentException(errorMsg);
                        }
                    } else {
                        String errorMsg = String.format(Locale.CHINA, "\"%d\" index conflicting, you wan't put array index into object key? please check path: %s", index, valueQueue);
                        throw new IllegalArgumentException(errorMsg);
                    }
                }
                default: {
                    break;
                }
            }
            current = child;
        }

        int lastStatus = Integer.valueOf(statusQueue.getLast().toString());
        switch (lastStatus) {
            case S_IN_FINISHED_OBJECT_KEY: {
                String key = valueQueue.getLast().toString();
                if (current instanceof QSObject) {
                    QSObject<String, Object> object = (QSObject<String, Object>) current;
                    if (object.get(key) != null) {
                        String warnMsg = String.format(Locale.CHINA, "\"%s\" key repeating, rewritten. please check path: %s", key, valueQueue);
                        Logger.warn(warnMsg);
                    }
                    object.put(key, pairValue);
                } else {
                    String errorMsg = String.format(Locale.CHINA, "\"%s\" key conflicting, you wan't put object key into array index? please check path: %s", key, valueQueue);
                    throw new IllegalArgumentException(errorMsg);
                }
                break;
            }
            case S_IN_FINISHED_ARRAY_INDEX: {
                int index = Integer.valueOf(valueQueue.getLast().toString());
                if (current instanceof QSArray) {
                    QSArray<Object> array = (QSArray<Object>) current;
                    if (index < array.size()) {
                        if (array.get(index) != null) {
                            String warnMsg = String.format(Locale.CHINA, "\"%s\" key repeating, rewritten. please check path: %s", index, valueQueue);
                            Logger.warn(warnMsg);
                        }
                        array.set(index, pairValue);
                    } else if (index == array.size()) {
                        array.add(pairValue);
                    } else {
                        String errorMsg = String.format(Locale.CHINA, "can't support skip add. please check path: %s", valueQueue);
                        throw new IllegalArgumentException(errorMsg);
                    }
                } else {
                    String errorMsg = String.format(Locale.CHINA, "\"%d\" index conflicting, you wan't put array index into object key? please check path: %s", index, valueQueue);
                    throw new IllegalArgumentException(errorMsg);
                }
            }
        }
    }

    private boolean hasPairValue(LinkedList<Integer> statusQueue) {
        return Integer.valueOf(statusQueue.getLast().toString()) == S_IN_FINISHED_VALUE;
    }

    private boolean isNaturalNumber(Object value) {
        try {
            int number = Integer.valueOf(value.toString());
            return number >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void nextToken() throws IOException {
        mToken = mLexer.yylex();
        if (mToken == null)
            mToken = new QSToken(QSToken.TYPE_EOF, null);
    }
}
