package com.mingyuans.smoke;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yanxq on 16/12/7.
 */

public class Smoke {

    private static SmokeSub SMOKE_SUB;

    public static void install(SmokeSub sub) {
        if (sub == null) {
            throw new IllegalArgumentException("Please do not input null !");
        }
        SMOKE_SUB = sub;
    }

    public static void install(String tag,PrintPlugin printPlugin) {
        SMOKE_SUB = new SmokeSub(null,tag,printPlugin);
        SMOKE_SUB.setExtraMethodElementIndex(1);
    }

    public static void setExtraMethodElementIndex(int extraIndex) {
        SMOKE_SUB.setExtraMethodElementIndex(extraIndex);
    }

    public static void enableConsoleOrWrite(Boolean consoleEnable, Boolean writeEnable) {
        SMOKE_SUB.enableConsoleOrWrite(consoleEnable,writeEnable);
    }

    public static void setLogPriority(int priority) {
        SMOKE_SUB.setLogPriority(priority);
    }

    public static void verbose() {
        SMOKE_SUB.verbose();
    }

    public static void verbose(Object object) {
        SMOKE_SUB.verbose(object);
    }

    public static void verbose(String message, Object... args) {
        SMOKE_SUB.verbose(message,args);
    }

    public static void debug() {
        SMOKE_SUB.debug();
    }

    public static void debug(String message, Object... args) {
        SMOKE_SUB.debug(message,args);
    }

    public static void debug(Object object) {
        SMOKE_SUB.debug(object);
    }

    public static void info() {
        SMOKE_SUB.info();
    }

    public static void info(String message, Object... args) {
       SMOKE_SUB.info(message,args);
    }

    public static void info(Object args) {
       SMOKE_SUB.info(args);
    }

    public static void warn() {
        SMOKE_SUB.warn();
    }

    public static void warn(String message, Object... args) {
        SMOKE_SUB.warn(message, args);
    }

    public static void warn(Throwable throwable,String message,Object... args) {
        SMOKE_SUB.warn(throwable, message, args);
    }

    public static void warn(Object args) {
        SMOKE_SUB.warn(args);
    }

    public static void error(String message, Object... args) {
        SMOKE_SUB.error(message,args);
    }

    public static void error(Object object) {
        SMOKE_SUB.error(object);
    }

    public static void error(Throwable throwable, String message, Object... args) {
        SMOKE_SUB.error(throwable, message, args);
    }

    public static SmokeSub newSub(String sub) {
        return SMOKE_SUB.newSub(sub);
    }

    public static SmokeSub newSub(String sub, Smoke.PrintPlugin plugin) {
        return SMOKE_SUB.newSub(sub, plugin);
    }

    public static void close() {
        SMOKE_SUB.close();
    }

    public static class LogInfo {
        public int level;
        public String message;
        public Object[] args;
        public String method;
        public String thread;
        public Throwable throwable;
        public List<String> subTags;

        public LogInfo(int level, String method,
                       String message, Object[] args,
                       Throwable throwable) {
            this.level = level;
            this.method = method;
            this.message = message;
            this.args = args;
            this.throwable = throwable;
            this.thread = Thread.currentThread().getName();
            this.subTags = new ArrayList<>();
        }
    }

    public static interface PrintPlugin {
        public String toString(LogInfo logInfo);
    }
}
