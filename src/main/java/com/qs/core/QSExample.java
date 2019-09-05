package com.qs.core;

import com.qs.core.log.Logger;
import com.qs.core.model.QSObject;
import com.qs.core.parser.ParseException;
import com.qs.core.parser.QSParser;

public class QSExample {

    public static void main(String[] args) {

        Logger.setDebug(true);

        String queryString = "&c1[b2][0][d1]=1&c1[b2][0][d2]=2&c1[b2][0][d2]=3&c1[b2][1][d1]=4";
        String queryString2 = "d=ab&d=12&d=&d=1&d=1,32,e2&f=1,2,3,4&f=5";
        String queryString3 = "d[][]=14&d[][]=fa&d[1][]=1"; // 仅用作测试，实际不建议这么使用，不够清晰
        String queryString4 = "a=1,2";
        try {
            QSObject<String, Object> qsObject = new QSParser().parse(queryString);
            System.out.println(qsObject);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}