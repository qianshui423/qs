package com.qs.core.parser;

public class QSToken {
    public static final int TYPE_VALUE = 1; // QSExample primitive value: string
    public static final int TYPE_EQUAL_SIGN = 2; // =
    public static final int TYPE_AND = 3; // &
    public static final int TYPE_EOF = -1; // end of file

    public int type;
    public String value;

    public QSToken(int type, String value) {
        this.type = type;
        this.value = value;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        switch (type) {
            case TYPE_VALUE:
                sb.append("VALUE(").append(value).append(")");
                break;
            case TYPE_EQUAL_SIGN:
                sb.append("EQUAL SIGN(=)");
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
