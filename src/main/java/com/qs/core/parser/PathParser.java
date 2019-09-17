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
        char zeroCH = key.charAt(0);
        if (zeroCH == TYPE_LEFT_SQUARE) {
            stack.push(zeroCH);
        } else if (zeroCH == TYPE_RIGHT_SQUARE) {
            throw new ParseException(position, ParseException.ERROR_PARSE_PATH_EXCEPTION, key);
        } else {
            pathEntityCollector.append(zeroCH);
        }
        for (int i = 1; i < key.length(); i++) {
            char ch = key.charAt(i);
            if (ch == TYPE_LEFT_SQUARE) {
                char preCh = key.charAt(i - 1);
                if (preCh != TYPE_RIGHT_SQUARE) {
                    path.add(pathEntityCollector.toString());
                    pathEntityCollector = new StringBuilder();
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
        if (pathEntityCollector.length() != 0) {
            path.add(pathEntityCollector.toString());
        }
        return path;
    }

}
