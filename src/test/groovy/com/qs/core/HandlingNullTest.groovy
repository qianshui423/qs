package com.qs.core

import com.qs.core.model.ParseOptions
import com.qs.core.model.QSObject
import com.qs.core.model.StringifyOptions
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class HandlingNullTest extends Specification {

    @Shared
    def nullValueObject = new QSObject()

    @Unroll
    def "null values are treated like empty strings"(QSObject input, String expect) {
        setup:
        nullValueObject.put("a", null)
        nullValueObject.put("b", "")

        expect:
        ObjectEqual.equals(nullValueObject.toQString(), expect)

        where:
        input           || expect
        nullValueObject || "a=&b="
    }

    @Shared
    def noNullValueObject = new QSObject()

    @Unroll
    def "parsing does not distinguish between parameters with and without equal signs"(String input, QSObject expect) {
        setup:
        noNullValueObject.put("a", "")
        noNullValueObject.put("b", "")

        expect:
        ObjectEqual.equals(QS.parse(input), expect)

        where:
        input  || expect
        "a&b=" || noNullValueObject
    }

    @Shared
    def nullValueObject2 = new QSObject()

    @Unroll
    def "to distinguish between null values and empty strings use the strictNullHandling flag"(QSObject input, String expect) {
        setup:
        nullValueObject2.put("a", null)
        nullValueObject2.put("b", "")

        expect:
        ObjectEqual.equals(nullValueObject2.toQString(new StringifyOptions.Builder().setStrictNullHandling(true).build()), expect)

        where:
        input            || expect
        nullValueObject2 || "a&b="
    }

    @Shared
    def nullValueObject3 = new QSObject()

    @Unroll
    def "to parse values without = back to null use the strictNullHandling flag"(String input, QSObject expect) {
        setup:
        nullValueObject3.put("a", null)
        nullValueObject3.put("b", "")

        expect:
        ObjectEqual.equals(QS.parse(input, new ParseOptions.Builder().setStrictNullHandling(true).build()), expect)

        where:
        input  || expect
        "a&b=" || nullValueObject3
    }

    @Shared
    def nullValueObject4 = new QSObject()

    @Unroll
    def "to completely skip rendering keys with null values, use the skipNulls flag"(QSObject input, String expect) {
        setup:
        nullValueObject4.put("a", "b")
        nullValueObject4.put("c", null)

        expect:
        ObjectEqual.equals(nullValueObject4.toQString(new StringifyOptions.Builder().setSkipNulls(true).build()), expect)

        where:
        input            || expect
        nullValueObject4 || "a=b"
    }
}
