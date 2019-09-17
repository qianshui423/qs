package com.qs.core.parser

import com.qs.core.QS
import com.qs.core.model.QSObject
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class UsageTest extends Specification {

//    {
//        a: 'c'
//    }
    @Shared
    def basicObject = new QSObject()
    @Shared
    def basicQString = "a=c"

    @Unroll
    def "qs usage"(String input, Object expect) {
        setup:
        basicObject.put("a", "c")

        expect:
        def result = QS.parse(input)
        ObjectEqual.equals(result, basicObject)
        ObjectEqual.equals(result.toQString(), basicQString)

        where:
        input         || expect
        basicQString || basicObject
    }
}
