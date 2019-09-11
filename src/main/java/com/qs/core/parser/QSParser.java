package com.qs.core.parser;

import com.qs.core.model.ParseOptions;
import com.qs.core.model.QSObject;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class QSParser {

    private static final int S_INIT = 0;
    private static final int S_IN_FINISHED_VALUE = 1;
    private static final int S_IN_FINISHED_EQUAL_SIGN = 2;
    private static final int S_IN_ERROR = -1;

    private static final String EMPTY_STRING = "";

    private QSLex mLexer = new QSLex(null);
    private QSToken mToken = null;
    private int mStatus = S_INIT;

    private void reset() {
        mToken = null;
        mStatus = S_INIT;
    }

    private void reset(Reader in) {
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
        skipQueryPrefix(in, options);
        reset(in);
        ParserHandler parserHandler = new ParserHandler(options);
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
                            if (parserHandler.isUpperLimit()) {
                                mToken = new QSToken(QSToken.TYPE_EOF, null);
                            } else {
                                parserHandler.pairKeyStart(mLexer.getPosition(), mToken);
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
                case S_IN_FINISHED_EQUAL_SIGN: {
                    switch (mToken.type) {
                        case QSToken.TYPE_VALUE: {
                            mStatus = S_IN_FINISHED_VALUE;
                            parserHandler.offerValue(mToken.value);
                            break;
                        }
                        case QSToken.TYPE_AND:
                        case QSToken.TYPE_EOF: {
                            mStatus = S_INIT;
                            parserHandler.offerValue(EMPTY_STRING);
                            parserHandler.pairValueEnd(mLexer.getPosition());
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
                        case QSToken.TYPE_EQUAL_SIGN: {
                            mStatus = S_IN_FINISHED_EQUAL_SIGN;
                            break;
                        }
                        case QSToken.TYPE_AND:
                        case QSToken.TYPE_EOF: {
                            mStatus = S_INIT;
                            if (!options.isStrictNullHandling()) {
                                parserHandler.offerValue(EMPTY_STRING);
                            }
                            parserHandler.pairValueEnd(mLexer.getPosition());
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

        return parserHandler.getQSObject();
    }

    private void skipQueryPrefix(Reader in, ParseOptions options) throws IOException {
        if (options.isIgnoreQueryPrefix()) {
            char[] head = new char[1];
            int length = in.read(head, 0, 1);
            if (length > 0) {
                if (head[0] != '?') {
                    in.reset();
                }
            }
        }
    }

    private void nextToken() throws IOException {
        mToken = mLexer.yylex();
        if (mToken == null)
            mToken = new QSToken(QSToken.TYPE_EOF, null);
    }
}
