package com.qs.core.parser;

public class QSToken {
    public static final int TYPE_VALUE = 1; // QSExample primitive value: string, number, boolean, null
    public static final int TYPE_LEFT_SQUARE = 3; // [
    public static final int TYPE_RIGHT_SQUARE = 4; // ]
    public static final int TYPE_EQUAL_SIGN = 2; // =
    public static final int TYPE_COMMA = 5; // ,
    public static final int TYPE_DOT = 7; // .
    public static final int TYPE_AND = 6; // &
    public static final int TYPE_EOF = -1; // end of file

    public int type;
    public Object value;

    public QSToken(int type, Object value) {
        this.type = type;
        this.value = value;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        switch (type) {
            case TYPE_VALUE:
                sb.append("VALUE(").append(value).append(")");
                break;
            case TYPE_LEFT_SQUARE:
                sb.append("LEFT SQUARE([)");
                break;
            case TYPE_RIGHT_SQUARE:
                sb.append("RIGHT SQUARE(])");
                break;
            case TYPE_EQUAL_SIGN:
                sb.append("EQUAL SIGN(=)");
                break;
            case TYPE_COMMA:
                sb.append("COMMA(,)");
                break;
            case TYPE_DOT:
                sb.append("DOT(.)");
                break;
            case TYPE_AND:
                sb.append("AND(&)");
                break;
            case TYPE_EOF:
                sb.append("END OF FILE");
                break;
        }
        return sb.toString();
    }
}
