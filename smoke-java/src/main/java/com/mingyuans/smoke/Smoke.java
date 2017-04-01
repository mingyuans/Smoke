package com.mingyuans.smoke;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yanxq on 16/12/7.
 */

public class Smoke {

    public static final int NONE = -1;
    public static final int VERBOSE = 2;
    public static final int DEBUG = 3;
    public static final int INFO = 4;
    public static final int WARN = 5;
    public static final int ERROR = 6;
    public static final int ASSERT = 7;

    private static SubSmoke SUB_SMOKE;

    public static void install(SubSmoke sub) {
        if (sub == null) {
            throw new IllegalArgumentException("Please do not input null !");
        }
        SUB_SMOKE = sub;
    }

    public static void install(String tag,Processes processes) {
        SUB_SMOKE = new SubSmoke(tag,null);
        SUB_SMOKE.setExtraMethodOffset(1);
        open();
    }

    public static void open() {
        SUB_SMOKE.open();
    }

    public static void enableConsole(boolean consoleEnable) {
        SUB_SMOKE.enableConsole(consoleEnable);
    }

    public static SubSmoke getImpl() {
        return SUB_SMOKE;
    }

    public static void setExtraMethodOffset(int extraIndex) {
        SUB_SMOKE.setExtraMethodOffset(extraIndex);
    }

    public static void setLogPriority(int priority) {
        SUB_SMOKE.setLogPriority(priority);
    }

    public static void verbose() {
        SUB_SMOKE.verbose();
    }

    public static void verbose(Object object) {
        SUB_SMOKE.verbose(object);
    }

    public static void verbose(String message, Object... args) {
        SUB_SMOKE.verbose(message,args);
    }

    public static void debug() {
        SUB_SMOKE.debug();
    }

    public static void debug(String message, Object... args) {
        SUB_SMOKE.debug(message,args);
    }

    public static void debug(Object object) {
        SUB_SMOKE.debug(object);
    }

    public static void info() {
        SUB_SMOKE.info();
    }

    public static void info(String message, Object... args) {
       SUB_SMOKE.info(message,args);
    }

    public static void info(Object args) {
       SUB_SMOKE.info(args);
    }

    public static void warn() {
        SUB_SMOKE.warn();
    }

    public static void warn(String message, Object... args) {
        SUB_SMOKE.warn(message, args);
    }

    public static void warn(Throwable throwable,String message,Object... args) {
        SUB_SMOKE.warn(throwable, message, args);
    }

    public static void warn(Object args) {
        SUB_SMOKE.warn(args);
    }

    public static void error(String message, Object... args) {
        SUB_SMOKE.error(message,args);
    }

    public static void error(Object object) {
        SUB_SMOKE.error(object);
    }

    public static void error(Throwable throwable, String message, Object... args) {
        SUB_SMOKE.error(throwable, message, args);
    }

    public static void log(int level, String tag, String message, Object... args) {
        SUB_SMOKE.log(level,tag,message,args);
    }

    public static void xml(int level,String tag,String xml) {
        SUB_SMOKE.xml(level,tag,xml);
    }

    public static void xml(int level,String xml) {
        SUB_SMOKE.xml(level,xml);
    }

    public static void json(int level,String tag, String json) {
        SUB_SMOKE.json(level,tag,json);
    }

    public static void json(int level,String json) {
        SUB_SMOKE.json(level,json);
    }

    public static SubSmoke newSub(String sub) {
        return SUB_SMOKE.newSub(sub);
    }

    public static SubSmoke newSub(String sub, Processes processes) {
        return SUB_SMOKE.newSub(sub, processes);
    }

    public static void close() {
        SUB_SMOKE.close();
    }

    public static class LogBean {
        public int level;
        public String tag;
        public String message;
        public Object[] args;
        public StackTraceElement traceElement;
        public Throwable throwable;
        public List<String> subList = new ArrayList<>();
        public String thread = Thread.currentThread().getName();
        public Map<String,Object> extras = new HashMap<>();

        public LogBean(int level, String tag, List<String> subList,
                       String message, StackTraceElement traceElement,
                       Throwable throwable, Object[] args) {
            this.level = level;
            this.tag = tag;
            this.message = message;
            this.args = args;
            this.traceElement = traceElement;
            this.throwable = throwable;
            CollectionUtil.addAll(this.subList, subList);
        }
    }

    public static abstract class Process {
        abstract public boolean proceed(LogBean logBean, List<String> messages,Chain chain);
        public void open() {};
        public void close() {};
        public boolean event(String event,Object value) {return false;}

        public static class Chain {
            private int index;
            private final Processes processes;
            public Chain(int index ,Processes processes) {
                this.index = index;
                this.processes = processes;
            }

            public boolean proceed(LogBean logBean, List<String> messages) {
                if (processes == null || index >= processes.size()) {
                    return true;
                }

                Process process = processes.get(index++);
                if (process != null) {
                    try {
                        if (!process.proceed(logBean,messages,this)) {
                            return false;
                        }
                    } catch (Throwable throwable) {
                        System.err.println("[Smoke] proceed error; \n " + StringUtil.throwable2String(throwable));
                    }
                }
                return true;
            }
        }
    }
}
