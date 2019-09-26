package com.qs.core

import com.google.gson.Gson
import com.qs.core.log.Logger
import com.qs.core.model.ParseOptions
import com.qs.core.model.QSArray
import com.qs.core.model.QSObject
import com.qs.core.parser.ParseException
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class InDepthTest extends Specification {

    @Shared
    def object = new QSObject()
    @Shared
    def object2 = new QSObject()

    @Unroll
    def "parse complex object"(String input, ParseOptions options, QSObject expect) {
        setup:
        def d1 = "1"
        def d2 = new QSArray()
        d2.add("2")
        d2.add("3")
        def b2Element1 = new QSObject()
        b2Element1.put("d1", d1)
        b2Element1.put("d2", d2)
        def d1Array = new QSArray()
        d1Array.add("4")
        d1Array.add(null)

        def b2Element2 = new QSObject()
        b2Element2.put("d1", d1Array)
        def b2Array = new QSArray()
        b2Array.add(b2Element1)
        b2Array.add(b2Element2)
        def b2Object = new QSObject()
        b2Object.put("b2", b2Array)
        object.put("c1", b2Object)

        def b2InObject = new QSObject()
        b2InObject.put("0", b2Element1)
        b2InObject.put("1", b2Element2)
        def b2WrapObject = new QSObject()
        b2WrapObject.put("b2", b2InObject)
        object2.put("c1", b2WrapObject)

        expect:
        ObjectEqual.equals(QS.parse(input, options), expect)

        where:
        input                                                                            || options                                                                              || expect
        "&c1[b2][0][d1]=1&c1[b2][0][d2]=2&c1[b2][0][d2]=3&c1[b2][1][d1]=4&c1[b2][1][d1]" || new ParseOptions.Builder().setStrictNullHandling(true).build()                       || object
        "&c1[b2][0][d1]=1&c1[b2][0][d2]=2&c1[b2][0][d2]=3&c1[b2][1][d1]=4&c1[b2][1][d1]" || new ParseOptions.Builder().setStrictNullHandling(true).setParseArrays(false).build() || object2
    }

    @Shared
    def object3 = new QSObject()
    @Shared
    def object4 = new QSObject()

    @Unroll
    def "parse comma object"(String input, ParseOptions options, QSObject expect) {
        setup:
        def d2 = "ab"
        def dArray = new QSArray()
        dArray.add("12")
        dArray.add("")
        dArray.add("1")
        dArray.add("1,32,e2")
        def fArray = new QSArray()
        fArray.add("1,2,3,4")
        fArray.add("5")
        object3.put("d2", d2)
        object3.put("d", dArray)
        object3.put("f", fArray)

        def dArray2 = new QSArray()
        dArray2.add("12")
        dArray2.add("")
        dArray2.add("1")
        dArray2.add("1")
        dArray2.add("32")
        dArray2.add("e2")
        def fArray2 = new QSArray()
        fArray2.add("1")
        fArray2.add("2")
        fArray2.add("3")
        fArray2.add("4")
        fArray2.add("5")
        object4.put("d2", d2)
        object4.put("d", dArray2)
        object4.put("f", fArray2)


        expect:
        ObjectEqual.equals(QS.parse(input, options), expect)

        where:
        input                                       || options                                                                        || expect
        "d2=ab&d=12&d=&d=1&d=1,32,e2&f=1,2,3,4&f=5" || new ParseOptions.Builder().setStrictNullHandling(true).setComma(false).build() || object3
        "d2=ab&d=12&d=&d=1&d=1,32,e2&f=1,2,3,4&f=5" || new ParseOptions.Builder().setStrictNullHandling(true).setComma(true).build()  || object4
    }

    @Shared
    def object5 = new QSObject()

    @Unroll
    def "parse brackets and indices object"(String input, ParseOptions options, QSObject expect) {
        setup:
        def dE0Array = new QSArray()
        dE0Array.add("14")
        def dE1Array = new QSArray()
        dE1Array.add("fa")
        dE1Array.add("1")
        def dInArray = new QSArray()
        dInArray.add(dE0Array)
        dInArray.add(dE1Array)
        object5.put("d", dInArray)

        expect:
        ObjectEqual.equals(QS.parse(input, options), expect)

        where:
        input                        || options                            || expect
        "d[][]=14&d[][]=fa&d[1][]=1" || new ParseOptions.Builder().build() || object5
    }

    @Shared
    def object6 = new QSObject()

    @Unroll
    def "parse dot object"(String input, ParseOptions options, QSObject expect) {
        setup:
        def dArray = new QSArray()
        dArray.add("1")
        dArray.add("2")
        def dInCObject = new QSObject()
        dInCObject.put("d", dArray)
        def cIn0Object = new QSObject()
        cIn0Object.put("c", dInCObject)

        def fObject = new QSObject()
        fObject.put("0", "3")
        fObject.put("g", "4")
        def fInCObject = new QSObject()
        fInCObject.put("f", fObject)
        def cInBObject = new QSObject()
        cInBObject.put("c", fInCObject)

        def aObject = new QSObject()
        aObject.put("0", cIn0Object)
        aObject.put("b", cInBObject)

        object6.put("a", aObject)


        expect:
        ObjectEqual.equals(QS.parse(input, options), expect)

        where:
        input                                          || options                                               || expect
        "a.0.c.d=1&a.0.c.d=2&a.b.c.f.0=3&&a.b.c.f.g=4" || new ParseOptions.Builder().setAllowDots(true).build() || object6
    }

    @Shared
    def object7 = new QSObject()

    @Unroll
    def "parse exception"(String input, ParseOptions options, QSObject expect) {
        setup:

        expect:
        try {
            QS.parse(input, options)
        } catch (ParseException e) {
            println(e)
            true
        }

        where:
        input    || options                            || expect
        "&a=&=&" || new ParseOptions.Builder().build() || object7
    }

    @Shared
    def object8 = new QSObject()

    @Unroll
    def "test toJsonString"() {
        setup:
        def d1 = "1"
        def d2 = new QSArray()
        d2.add("2")
        d2.add("3")
        def b2Element1 = new QSObject()
        b2Element1.put("d1", d1)
        b2Element1.put("d2", d2)
        def d1Array = new QSArray()
        d1Array.add("4")
        d1Array.add(null)

        def b2Element2 = new QSObject()
        b2Element2.put("d1", d1Array)
        def b2Array = new QSArray()
        b2Array.add(b2Element1)
        b2Array.add(b2Element2)
        def b2Object = new QSObject()
        b2Object.put("b2", b2Array)
        object.put("c1", b2Object)

        def b2InObject = new QSObject()
        b2InObject.put("0", b2Element1)
        b2InObject.put("1", b2Element2)
        def b2WrapObject = new QSObject()
        b2WrapObject.put("b2", b2InObject)
        object8.put("c1", b2WrapObject)

        expect:
        ObjectEqual.equals(QS.toJsonString(object8), new Gson().toJson(object8))
    }

    @Shared
    def object9 = new QSObject()

    @Unroll
    def "qs formatter"() {
        setup:
        def d1 = "1"
        def d2 = new QSArray()
        d2.add("2")
        d2.add("3")
        def b2Element1 = new QSObject()
        b2Element1.put("d1", d1)
        b2Element1.put("d2", d2)
        def d1Array = new QSArray()
        d1Array.add("4")
        d1Array.add(null)

        def b2Element2 = new QSObject()
        b2Element2.put("d1", d1Array)
        def b2Array = new QSArray()
        b2Array.add(b2Element1)
        b2Array.add(b2Element2)
        def b2Object = new QSObject()
        b2Object.put("b2", b2Array)
        object.put("c1", b2Object)

        def b2InObject = new QSObject()
        b2InObject.put("0", b2Element1)
        b2InObject.put("1", b2Element2)
        def b2WrapObject = new QSObject()
        b2WrapObject.put("b2", b2InObject)
        object9.put("c1", b2WrapObject)

        expect:
        ObjectEqual.equals(object9.toFormatString(), "{\n" +
                "\tc1:{\n" +
                "\t\tb2:{\n" +
                "\t\t\t0:{\n" +
                "\t\t\t\td1:\u001B[34;0m\"1\"\u001B[30;0m,\n" +
                "\t\t\t\td2:[\n" +
                "\t\t\t\t\t\u001B[34;0m\"2\"\u001B[30;0m,\n" +
                "\t\t\t\t\t\u001B[34;0m\"3\"\u001B[30;0m\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t1:{\n" +
                "\t\t\t\td1:[\n" +
                "\t\t\t\t\t\u001B[34;0m\"4\"\u001B[30;0m,\n" +
                "\t\t\t\t\t\u001B[34;0mnull\u001B[30;0m\n" +
                "\t\t\t\t]\n" +
                "\t\t\t}\n" +
                "\t\t}\n" +
                "\t}\n" +
                "}")
    }

    @Unroll
    def "qs Logger"() {
        setup:
        Logger.setDebug(true)

        expect:
        Logger.isDebug()
    }
}
