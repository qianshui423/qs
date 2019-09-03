package com.qs.core;

import com.qs.core.log.Logger;
import com.qs.core.model.QSObject;
import com.qs.core.parser.ParseException;
import com.qs.core.parser.QSParser;

public class QSExample {

    public static void main(String[] args) {

        Logger.setDebug(true);

        String queryString = "&c1[b2][0][d1]=1&c1[b2][0][d2]=2&c1[b2][0][d2]=3&c1[b2][1][d1]=4&d[0]=1";
        //String queryString1 =  "d[0][]=ab&d[0][]=cd";
        //String queryString2 = "d=ab&d=&d=fa&f=";
        String queryString2 = "d[]=1&d[]=ab";
        try {
            QSObject<String, Object> qsObject = new QSParser().parse(queryString2);
            System.out.println(qsObject);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
