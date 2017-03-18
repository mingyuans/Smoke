package com.mingyuans.smoke;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

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

    private static SmokeSub SMOKE_SUB;

    public static void install(SmokeSub sub) {
        if (sub == null) {
            throw new IllegalArgumentException("Please do not input null !");
        }
        SMOKE_SUB = sub;
        SmokeUncaughtErrorHandler.register(sub.mContext);
    }

    public static void install(Context context,String tag) {
        SMOKE_SUB = new SmokeSub(context,tag,null);
        SMOKE_SUB.setExtraMethodOffset(1);

        SmokeUncaughtErrorHandler.register(context.getApplicationContext());
    }

    public static void setExtraMethodOffset(int extraIndex) {
        SMOKE_SUB.setExtraMethodOffset(extraIndex);
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

    public static void log(int level, String tag, String message, Object... args) {
        SMOKE_SUB.log(level,tag,message,args);
    }

    public static void xml(int level,String tag,String xml) {
        SMOKE_SUB.xml(level,tag,xml);
    }

    public static void xml(int level,String xml) {
        SMOKE_SUB.xml(level,xml);
    }

    public static void json(int level,String tag, String json) {
        SMOKE_SUB.json(level,tag,json);
    }

    public static void json(int level,String json) {
        SMOKE_SUB.json(level,json);
    }

    public static SmokeSub newSub(String sub) {
        return SMOKE_SUB.newSub(sub);
    }

    public static SmokeSub newSub(String sub, Processes processes) {
        return SMOKE_SUB.newSub(sub, processes);
    }

    public static void close() {
        SMOKE_SUB.close();
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
        public void open(Context context) {};
        public void close() {};

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
                        Log.e("Smoke","proceed error",throwable);
                    }
                }
                return true;
            }
        }
    }
}
