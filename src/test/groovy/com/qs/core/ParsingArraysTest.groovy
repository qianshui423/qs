package com.qs.core

import com.qs.core.model.ArrayFormat
import com.qs.core.model.ParseOptions
import com.qs.core.model.QSArray
import com.qs.core.model.QSObject
import com.qs.core.model.StringifyOptions
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class ParsingArraysTest extends Specification {

    //    { a: ['b', 'c'] }
    @Shared
    def noIndexFormatObject = new QSObject()
    @Shared
    def noIndexFormatQString = "a[]=b&a[]=c"

    @Unroll
    def "parse and stringify for no index array format case"(String input, Object expect) {
        setup:
        def aArray = new QSArray()
        aArray.add("b")
        aArray.add("c")
        noIndexFormatObject.put("a", aArray)

        expect:
        def result = QS.parse(input)
        ObjectEqual.equals(result, noIndexFormatObject)
        ObjectEqual.equals(result.toQString(new StringifyOptions.Builder().setEncode(false).setArrayFormat(ArrayFormat.BRACKETS).build()), noIndexFormatQString)

        where:
        input                || expect
        noIndexFormatQString || noIndexFormatObject
    }

//    { a: ['b', 'c'] }
    @Shared
    def indexFormatObject = new QSObject()
    @Shared
    def indexFormatQString = "a[0]=b&a[1]=c"

    @Unroll
    def "parse and stringify for index array format case"(String input, Object expect) {
        setup:
        def aArray = new QSArray()
        aArray.add("b")
        aArray.add("c")
        indexFormatObject.put("a", aArray)

        expect:
        def result = QS.parse(input)
        ObjectEqual.equals(result, indexFormatObject)
        ObjectEqual.equals(result.toQString(new StringifyOptions.Builder().setEncode(false).setArrayFormat(ArrayFormat.INDICES).build()), indexFormatQString)

        where:
        input              || expect
        indexFormatQString || indexFormatObject
    }

//    { a: ['', 'b'] }
    @Shared
    def withEmptyStringObject = new QSObject()
    @Shared
    def withEmptyStringQString = "a[]=&a[]=b"

    @Unroll
    def "parse and stringify for empty string case"(String input, Object expect) {
        setup:
        def aArray = new QSArray()
        aArray.add("")
        aArray.add("b")
        withEmptyStringObject.put("a", aArray)

        expect:
        def result = QS.parse(input)
        ObjectEqual.equals(result, withEmptyStringObject)
        ObjectEqual.equals(result.toQString(new StringifyOptions.Builder().setEncode(false).setArrayFormat(ArrayFormat.BRACKETS).build()), withEmptyStringQString)

        where:
        input                  || expect
        withEmptyStringQString || withEmptyStringObject
    }

//    { a: ['b', '', 'c'] }
    @Shared
    def withIndexEmptyStringObject = new QSObject()
    @Shared
    def withIndexEmptyStringQString = "a[0]=b&a[1]=&a[2]=c"

    @Unroll
    def "parse and stringify for index empty string case"(String input, Object expect) {
        setup:
        def aArray = new QSArray()
        aArray.add("b")
        aArray.add("")
        aArray.add("c")
        withIndexEmptyStringObject.put("a", aArray)

        expect:
        def result = QS.parse(input)
        ObjectEqual.equals(result, withIndexEmptyStringObject)
        ObjectEqual.equals(result.toQString(new StringifyOptions.Builder().setEncode(false).setArrayFormat(ArrayFormat.INDICES).build()), withIndexEmptyStringQString)

        where:
        input                       || expect
        withIndexEmptyStringQString || withIndexEmptyStringObject
    }

    //    { a: { '0': 'b' } }
    @Shared
    def parseArraysObject = new QSObject()
    @Shared
    def parseArraysQString = "a[0]=b"

    @Unroll
    def "parse and stringify for parseArrays case"(String input, Object expect) {
        setup:
        def aObject = new QSObject()
        aObject.put("0", "b")
        parseArraysObject.put("a", aObject)

        expect:
        def result = QS.parse(input, new ParseOptions.Builder().setParseArrays(false).build())
        ObjectEqual.equals(result, parseArraysObject)
        ObjectEqual.equals(result.toQString(new StringifyOptions.Builder().setEncode(false).build()), parseArraysQString)

        where:
        input   || expect
        "a[]=b" || parseArraysObject
    }

    //    { a: { '0': 'b', b: 'c' } }
    @Shared
    def mixNotationsObject = new QSObject()
    @Shared
    def mixNotationsQString = "a[0]=b&a[b]=c"

    @Unroll
    def "parse and stringify for mix notations case"(String input, Object expect) {
        setup:
        def aObject = new QSObject()
        aObject.put("0", "b")
        aObject.put("b", "c")
        mixNotationsObject.put("a", aObject)

        expect:
        def result = QS.parse(input)
        ObjectEqual.equals(result, mixNotationsObject)
        ObjectEqual.equals(result.toQString(new StringifyOptions.Builder().setEncode(false).build()), mixNotationsQString)

        where:
        input               || expect
        mixNotationsQString || mixNotationsObject
    }

    //    { a: [{ b: 'c' }] }
    @Shared
    def objectsArrayObject = new QSObject()
    @Shared
    def objectsArrayQString = "a[][b]=c"

    @Unroll
    def "parse and stringify for objects array case"(String input, Object expect) {
        setup:
        def arrayObject = new QSObject()
        arrayObject.put("b", "c")
        def aArray = new QSArray()
        aArray.add(arrayObject)
        objectsArrayObject.put("a", aArray)

        expect:
        def result = QS.parse(input)
        ObjectEqual.equals(result, objectsArrayObject)
        ObjectEqual.equals(result.toQString(new StringifyOptions.Builder().setEncode(false).setArrayFormat(ArrayFormat.BRACKETS).build()), objectsArrayQString)

        where:
        input               || expect
        objectsArrayQString || objectsArrayObject
    }

    //    { a: ['b', 'c'] }
    @Shared
    def commaArrayObject = new QSObject()
    @Shared
    def commaArrayQString = "a=b,c"

    @Unroll
    def "parse and stringify for comma array case"(String input, Object expect) {
        setup:
        def aArray = new QSArray()
        aArray.add("b")
        aArray.add("c")
        commaArrayObject.put("a", aArray)

        expect:
        def result = QS.parse(input, new ParseOptions.Builder().setComma(true).build())
        ObjectEqual.equals(result, commaArrayObject)
        ObjectEqual.equals(result.toQString(new StringifyOptions.Builder().setEncode(false).setArrayFormat(ArrayFormat.COMMA).build()), commaArrayQString)

        where:
        input               || expect
        commaArrayQString || commaArrayObject
    }
}
