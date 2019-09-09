package com.qs.core.parser;

import com.qs.core.model.ArrayFormat;
import com.qs.core.model.ParseOptions;
import com.qs.core.model.QSObject;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class QSParser {

    public static final int S_INIT = 0;
    public static final int S_IN_FINISHED_LEFT_SQUARE = 1;
    public static final int S_IN_FINISHED_RIGHT_SQUARE = 2;
    public static final int S_IN_FINISHED_VALUE = 3;
    public static final int S_IN_FINISHED_EQUAL_SIGN = 4;
    public static final int S_IN_FINISHED_COMMA = 5;
    public static final int S_IN_FINISHED_DOT = 6;
    public static final int S_IN_ERROR = -1;

    public static final String EMPTY_STRING = "";

    private QSLex mLexer = new QSLex(null);
    private QSToken mToken = null;
    private int mStatus = S_INIT;

    private ParserHandler mParserHandler = new ParserHandler();

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

    public QSObject parse(String s) throws ParseException {
        return parse(s, new ParseOptions.Builder().build());
    }

    public QSObject parse(String s, ParseOptions options) throws ParseException {
        StringReader in = new StringReader(s);
        try {
            return parse(in, options);
        } catch (IOException e) {
            throw new ParseException(S_IN_ERROR, ParseException.ERROR_UNEXPECTED_EXCEPTION, e);
        }
    }

    public QSObject parse(Reader in, ParseOptions options) throws IOException, ParseException {
        reset(in);
        do {
            nextToken();
            switch (mStatus) {
                case S_INIT: {
                    switch (mToken.type) {
                        case QSToken.TYPE_AND:
                        case QSToken.TYPE_EOF: {
                            break;
                        }
                        case QSToken.TYPE_VALUE: {
                            mStatus = S_IN_FINISHED_VALUE;
                            mParserHandler.pairKeyStart(options, mToken);
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
                            mStatus = S_IN_FINISHED_VALUE;
                            mParserHandler.offerPath(mToken.value);
                            mParserHandler.switchMode(ArrayFormat.INDICES);
                            break;
                        }
                        case QSToken.TYPE_RIGHT_SQUARE: {
                            mStatus = S_IN_FINISHED_RIGHT_SQUARE;
                            mParserHandler.offerPath(ParserHandler.BRACKETS_EMPTY_INDEX);
                            mParserHandler.switchMode(ArrayFormat.BRACKETS);
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
                            mParserHandler.offerValue(mToken.value);
                            break;
                        }
                        case QSToken.TYPE_AND:
                        case QSToken.TYPE_EOF: {
                            mStatus = S_INIT;
                            if (mParserHandler.isCommaMode()) {
                                mParserHandler.switchMode(ArrayFormat.REPEAT);
                            }
                            mParserHandler.offerValue(EMPTY_STRING);
                            mParserHandler.pairValueEnd(mLexer.getPosition());
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
                    switch (mToken.type) {
                        case QSToken.TYPE_LEFT_SQUARE: {
                            mStatus = S_IN_FINISHED_LEFT_SQUARE;
                            mParserHandler.switchMode(ArrayFormat.INDICES);
                            break;
                        }
                        case QSToken.TYPE_RIGHT_SQUARE: {
                            mStatus = S_IN_FINISHED_RIGHT_SQUARE;
                            break;
                        }
                        case QSToken.TYPE_EQUAL_SIGN: {
                            mStatus = S_IN_FINISHED_EQUAL_SIGN;
                            mParserHandler.switchMode(ArrayFormat.COMMA);
                            break;
                        }
                        case QSToken.TYPE_AND:
                        case QSToken.TYPE_EOF: {
                            mStatus = S_INIT;
                            if (mParserHandler.isCommaMode()) {
                                mParserHandler.switchMode(ArrayFormat.REPEAT);
                            }
                            if (!options.isStrictNullHandling()) {
                                mParserHandler.offerValue(EMPTY_STRING);
                            }
                            mParserHandler.pairValueEnd(mLexer.getPosition());
                            break;
                        }
                        case QSToken.TYPE_COMMA: {
                            mStatus = S_IN_FINISHED_COMMA;
                            break;
                        }
                        case QSToken.TYPE_DOT: {
                            mStatus = S_IN_FINISHED_DOT;
                            if (!options.isAllowDots()) {
                                mParserHandler.appendLastPath(mToken.value);
                            }
                            break;
                        }
                        default: {
                            mStatus = S_IN_ERROR;
                            break;
                        }
                    }
                    break;
                }
                case S_IN_FINISHED_COMMA: {
                    if (mToken.type == QSToken.TYPE_VALUE) {
                        mStatus = S_IN_FINISHED_VALUE;
                        mParserHandler.offerValue(mToken.value);
                    } else {
                        mStatus = S_IN_ERROR;
                    }
                    break;
                }
                case S_IN_FINISHED_DOT: {
                    switch (mToken.type) {
                        case QSToken.TYPE_VALUE: {
                            mStatus = S_IN_FINISHED_VALUE;
                            if (options.isAllowDots()) {
                                mParserHandler.offerPath(mToken.value);
                            } else {
                                mParserHandler.appendLastPath(mToken.value);
                            }
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
                case S_IN_ERROR:
                    throw new ParseException(getPosition(), ParseException.ERROR_UNEXPECTED_TOKEN, mToken);
            }
            if (mStatus == S_IN_ERROR) {
                throw new ParseException(getPosition(), ParseException.ERROR_UNEXPECTED_TOKEN, mToken);
            }
        } while (mToken.type != QSToken.TYPE_EOF);

        QSObject qsObject = mParserHandler.getQSObject();
        mParserHandler.reset();

        return qsObject;
    }

    private void nextToken() throws IOException {
        mToken = mLexer.yylex();
        if (mToken == null)
            mToken = new QSToken(QSToken.TYPE_EOF, null);
    }
}
