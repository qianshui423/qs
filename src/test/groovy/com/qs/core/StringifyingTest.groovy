package com.qs.core

import com.qs.core.model.ArrayFormat
import com.qs.core.model.QSArray
import com.qs.core.model.QSObject
import com.qs.core.model.StringifyOptions
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class StringifyingTest extends Specification {

    @Shared
    def noNestedObject = new QSObject()
    @Shared
    def nestedObject = new QSObject()
    @Shared
    def encodeValuesOnlyObject = new QSObject()
    @Shared
    def basicArrayObject = new QSObject()
    @Shared
    def arrayFormatObject = new QSObject()
    @Shared
    def multiNestedObject = new QSObject()
    @Shared
    def emptyValueObject = new QSObject()
    @Shared
    def emptyArrayObject = new QSObject()
    @Shared
    def emptyChildObject = new QSObject()
    @Shared
    def emptyArrayChildObject = new QSObject()
    @Shared
    def emptyNestedArrayObject = new QSObject()
    @Shared
    def emptyNestedChildObject = new QSObject()

    @Unroll
    def "stringify case"(QSObject input, StringifyOptions options, String expect) {
        setup:
        noNestedObject.put("a", "b")

        def bObject = new QSObject()
        bObject.put("b", "c")
        nestedObject.put("a", bObject)

        encodeValuesOnlyObject.put("a", "b")
        def cArray = new QSArray()
        cArray.add("d")
        cArray.add("e=f")
        encodeValuesOnlyObject.put("c", cArray)
        def fArray = new QSArray()
        def childArray1 = new QSArray()
        childArray1.add("g")
        def childArray2 = new QSArray()
        childArray2.add("h")
        fArray.add(childArray1)
        fArray.add(childArray2)
        encodeValuesOnlyObject.put("f", fArray)

        def aArray = new QSArray()
        aArray.add("b")
        aArray.add("c")
        aArray.add("d")
        basicArrayObject.put("a", aArray)

        def aCloneArray = new QSArray(aArray)
        aCloneArray.removeLast()
        arrayFormatObject.put("a", aCloneArray)

        def bMultiObject = new QSObject()
        bMultiObject.put("c", "d")
        bMultiObject.put("e", "f")
        def aMultiObject = new QSObject()
        aMultiObject.put("b", bMultiObject)
        multiNestedObject.put("a", aMultiObject)

        emptyValueObject.put("a", "")

        emptyArrayObject.put("a", new QSArray())

        emptyChildObject.put("a", new QSArray())

        def emptyCloneArray = new QSArray()
        emptyCloneArray.add(new QSArray())
        emptyArrayChildObject.put("a", emptyCloneArray)

        def bEmptyArrayObject = new QSObject()
        bEmptyArrayObject.put("b", new QSArray())
        emptyNestedArrayObject.put("a", bEmptyArrayObject)

        def bEmptyChildObject = new QSObject()
        bEmptyChildObject.put("b", new QSObject())
        emptyNestedChildObject.put("a", bEmptyChildObject)

        expect:
        ObjectEqual.equals(input.toQString(options), expect)

        where:
        input                  || options                                                                                      || expect
        noNestedObject         || new StringifyOptions.Builder().build()                                                       || "a=b"
        nestedObject           || new StringifyOptions.Builder().build()                                                       || "a%5Bb%5D=c"
        nestedObject           || new StringifyOptions.Builder().setEncode(false).build()                                      || "a[b]=c"
        encodeValuesOnlyObject || new StringifyOptions.Builder().setEncodeValuesOnly(true).build()                             || "a=b&c[0]=d&c[1]=e%3Df&f[0][0]=g&f[1][0]=h"
        basicArrayObject       || new StringifyOptions.Builder().setEncode(false).build()                                      || "a[0]=b&a[1]=c&a[2]=d"
        arrayFormatObject      || new StringifyOptions.Builder().setEncode(false).setArrayFormat(ArrayFormat.INDICES).build()  || "a[0]=b&a[1]=c"
        arrayFormatObject      || new StringifyOptions.Builder().setEncode(false).setArrayFormat(ArrayFormat.BRACKETS).build() || "a[]=b&a[]=c"
        arrayFormatObject      || new StringifyOptions.Builder().setEncode(false).setArrayFormat(ArrayFormat.REPEAT).build()   || "a=b&a=c"
        arrayFormatObject      || new StringifyOptions.Builder().setEncode(false).setArrayFormat(ArrayFormat.COMMA).build()    || "a=b,c"
        multiNestedObject      || new StringifyOptions.Builder().setEncode(false).build()                                      || "a[b][c]=d&a[b][e]=f"
        multiNestedObject      || new StringifyOptions.Builder().setEncode(false).setAllowDots(true).build()                   || "a.b.c=d&a.b.e=f"
        emptyValueObject       || new StringifyOptions.Builder().build()                                                       || "a="
        emptyArrayObject       || new StringifyOptions.Builder().build()                                                       || ""
        emptyChildObject       || new StringifyOptions.Builder().build()                                                       || ""
        emptyArrayChildObject  || new StringifyOptions.Builder().build()                                                       || ""
        emptyNestedArrayObject || new StringifyOptions.Builder().build()                                                       || ""
        emptyNestedChildObject || new StringifyOptions.Builder().build()                                                       || ""
    }
}
