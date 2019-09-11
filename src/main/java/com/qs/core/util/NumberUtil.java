package com.qs.core.util;

public class NumberUtil {
    /**
     * 判断 value 是否是自然数
     *
     * @param value
     * @return
     */
    public static boolean isNaturalNumber(String value) {
        try {
            int number = Integer.valueOf(value);
            return number >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
