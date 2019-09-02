package com.qs.core.log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Logger {

    private static boolean sDebug = false;

    private static final Map<Level, ColorString> sLevelToColor = new HashMap<Level, ColorString>() {{
        put(Level.INFO, ColorString.BLACK);
        put(Level.WARN, ColorString.YELLOW);
        put(Level.ERROR, ColorString.RED);
    }};

    public static void setDebug(boolean debug) {
        sDebug = debug;
    }

    public static void info(String log) {
        log(Level.INFO, log);
    }

    public static void warn(String log) {
        log(Level.WARN, log);
    }

    public static void error(String log) {
        log(Level.ERROR, log);
    }

    private static void log(Level level, String log) {
        if (!sDebug) return;
        String date = SimpleDateFormat.getDateTimeInstance().format(new Date());
        System.out.format(String.format(Locale.CHINA, "%s%s [%s]: %s%s\n", sLevelToColor.get(level).getCode(), date, level.name(), log, ColorString.BLACK.getCode()));
    }

    private enum Level {
        INFO, WARN, ERROR
    }
}
