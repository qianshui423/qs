package com.qs.core;

class ObjectEqual {

    static def equals(Object result, Object expect) {
        if (result == null ^ expect == null) return false
        if (result == null && expect == null) return true
        return result.toString().equals(expect.toString())
    }
}
