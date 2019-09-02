package com.qs.core

import spock.lang.Specification
import spock.lang.Unroll;


public class SpockTest extends Specification {
//    def "maximum of two numbers"() {
//        expect:
//        // exercise math method for a few different inputs
//        Math.max(1, 3) == 3
//        Math.max(7, 4) == 7
//        Math.max(0, 0) == 0
//    }

    def "maximum of two numbers"(int a, int b, int c) {
        expect:
        Math.max(a, b) == c

        where:
        a | b | c
        1 | 3 | 3
        7 | 4 | 7
        0 | 0 | 0
    }

    @Unroll
    def "maximum of two numbers"() {
        expect:
        Math.max(a, b) == c

        where:
        a | b || c
        1 | 3 || 3
        7 | 4 || 7
        0 | 0 || 0
    }
}
