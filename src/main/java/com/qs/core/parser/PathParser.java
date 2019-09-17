package com.qs.core.parser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

public class PathParser {

    private static final char TYPE_LEFT_SQUARE = '[';
    private static final char TYPE_RIGHT_SQUARE = ']';

    public static List<String> parse(String key, int position) throws ParseException {
        if (key == null || "".equals(key)) {
            throw new ParseException(position, ParseException.ERROR_PARSE_PATH_EXCEPTION, key);
        }
        LinkedList<Character> stack = new LinkedList<>();
        List<String> path = new ArrayList<>();
        StringBuilder pathEntityCollector = new StringBuilder();
        // 取出第一个path
        int index = 0;
        for (; index < key.length(); index++) {
            char ch = key.charAt(index);
            if (ch == TYPE_LEFT_SQUARE) {
                break;
            } else {
                pathEntityCollector.append(ch);
            }
        }
        path.add(pathEntityCollector.toString());
        if (index == key.length()) return path;
        pathEntityCollector = new StringBuilder();
        for (; index < key.length(); index++) {
            char ch = key.charAt(index);
            if (ch == TYPE_LEFT_SQUARE) {
                if (!stack.isEmpty()) {
                    pathEntityCollector.append(ch);
                }
                stack.push(ch);
            } else if (ch == TYPE_RIGHT_SQUARE) {
                try {
                    stack.pop();
                } catch (NoSuchElementException e) {
                    throw new ParseException(position, ParseException.ERROR_PARSE_PATH_EXCEPTION, key);
                }
                if (stack.isEmpty()) {
                    path.add(pathEntityCollector.toString());
                    pathEntityCollector = new StringBuilder();
                } else {
                    pathEntityCollector.append(ch);
                }
            } else {
                pathEntityCollector.append(ch);
            }
        }
        if (!stack.isEmpty()) throw new ParseException(position, ParseException.ERROR_PARSE_PATH_EXCEPTION, key);
        return path;
    }

}
