package com.qs.core;

import com.qs.core.log.Logger;
import com.qs.core.model.ArrayFormat;
import com.qs.core.model.ParseOptions;
import com.qs.core.model.QSObject;
import com.qs.core.model.StringifyOptions;
import com.qs.core.parser.ParseException;

public class QSExample {

    public static void main(String[] args) {

        Logger.setDebug(true);

        String queryString = "&c1[b2][0][d1]=1&c1[b2][0][d2]=2&c1[b2][0][d2]=3&c1[b2][1][d1]=4&c1[b2][1][d1]";
        String queryString2 = "d2=ab&d=12&d=&d=1&d=1,32,e2&f=1,2,3,4&f=5";
        String queryString3 = "d[][]=14&d[][]=fa&d[1][]=1"; // 仅用作测试，实际不建议这么使用，不够清晰
        String queryString4 = "a.0.c.d=1&a.0.c.d=2&a.b.c.f.0=3&&a.b.c.f.g=4"; // a.0.c.d=1&a.0.c.d=2&a.b.c.f=3
        String queryString5 = "c1[0][0]=0&c1[0][1]=1&c1[0][2]=2&c1[1][0]=null"; // c1[0][0]=0&c1[0][1]=1&c1[0][2]=2&c1[1][0]=2
        String queryString6 = "?a.?b.c.d.e.f.g.h.i";
        String queryString7 = "a[]=b";
        String queryString8 = "a.b=b.c";
        String queryString9 = "...a...b...=,,a,,b,,";
        String queryString10 = "c1[b2][0][d1]=1&c1[b2][0][d2]=2";
        String queryString11 = "&a=&=&";
        try {
            QSObject qsObject = QS.parse(queryString,
                    new ParseOptions.Builder()
                            .setDepth(10)
                            .setComma(true)
                            .setAllowDots(true)
                            .setStrictNullHandling(true)
                            .setIgnoreQueryPrefix(true)
                            .setParseArrays(false)
                            .build());
            System.out.println(qsObject.toFormatString());

            String qsString = qsObject.toQString(
                    new StringifyOptions.Builder()
                            .setStrictNullHandling(true)
                            .setEncode(false)
                            .setAddQueryPrefix(true)
                            .setSkipNulls(true)
                            .setArrayFormat(ArrayFormat.COMMA)
                            .build());
            System.out.println(qsString);


            QSObject qsObject1 = QS.parse(qsString,
                    new ParseOptions.Builder()
                            .setDepth(10)
                            .setComma(true)
                            .setAllowDots(true)
                            .setStrictNullHandling(true)
                            .setIgnoreQueryPrefix(true)
                            .setParseArrays(true)
                            .build());
            System.out.println(qsObject1.toFormatString());

            String jsonString = qsObject.toJsonString();
            System.out.println(jsonString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
