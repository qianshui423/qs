package com.qs.core.parser;

public class ParseException extends Exception {

    public static final int ERROR_UNEXPECTED_CHAR = 0;
    public static final int ERROR_UNEXPECTED_TOKEN = 1;
    public static final int ERROR_UNEXPECTED_EXCEPTION = 2;
    public static final int ERROR_SKIP_ADD_EXCEPTION = 3;
    public static final int ERROR_PARSE_PATH_EXCEPTION = 4;

    private int mErrorType;
    private Object mUnexpectedObject;
    private int mPosition;

    public ParseException(int mErrorType) {
        this(-1, mErrorType, null);
    }

    public ParseException(int errorType, Object unexpectedObject) {
        this(-1, errorType, unexpectedObject);
    }

    public ParseException(int position, int errorType, Object unexpectedObject) {
        this.mPosition = position;
        this.mErrorType = errorType;
        this.mUnexpectedObject = unexpectedObject;
    }

    public int getErrorType() {
        return mErrorType;
    }

    public void setErrorType(int errorType) {
        this.mErrorType = errorType;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        this.mPosition = position;
    }

    public Object getUnexpectedObject() {
        return mUnexpectedObject;
    }

    public void setUnexpectedObject(Object unexpectedObject) {
        this.mUnexpectedObject = unexpectedObject;
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        switch (mErrorType) {
            case ERROR_UNEXPECTED_CHAR:
                sb.append("Unexpected character (").append(mUnexpectedObject).append(") at position ").append(mPosition).append(".");
                break;
            case ERROR_UNEXPECTED_TOKEN:
                sb.append("Unexpected token ").append(mUnexpectedObject).append(" at position ").append(mPosition).append(".");
                break;
            case ERROR_UNEXPECTED_EXCEPTION:
                sb.append("Unexpected exception at position ").append(mPosition).append(": ").append(mUnexpectedObject);
                break;
            case ERROR_SKIP_ADD_EXCEPTION:
                sb.append("skip add exception at position ").append(mPosition).append(". ").append("can't support skip add. please check path").append(": ").append(mUnexpectedObject);
                break;
            case ERROR_PARSE_PATH_EXCEPTION:
                sb.append("parse path exception at position ").append(mPosition).append(". ").append("bracket not in couples. please check path").append(": ").append(mUnexpectedObject);
                break;
            default:
                sb.append("Unkown error at position ").append(mPosition).append(".");
                break;
        }
        return sb.toString();
    }
}
