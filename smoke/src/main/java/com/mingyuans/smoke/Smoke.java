package com.mingyuans.smoke;

import android.text.TextUtils;
import android.util.Log;

/**
 * Created by yanxq on 16/12/7.
 */

public class Smoke {
    private static String GLOBAL_TAG = "Smoke";

    private static int LOG_PRIORITY = Log.VERBOSE;
    private static PrintPlugin PRINT_PLUGIN = new DefaultPrintPlugin();

    public static void install(String globalTAG,PrintPlugin printPlugin) {
        if (!TextUtils.isEmpty(globalTAG)) {
            GLOBAL_TAG = globalTAG;
        }
        if (printPlugin != null) {
            PRINT_PLUGIN = printPlugin;
        }
    }

    public static void setLogPriority(int logLevel) {
        LOG_PRIORITY = logLevel;
    }

    public static void verbose() {
        println(Log.VERBOSE,null,null,null);
    }

    public static void verbose(String message, Object... args) {
        println(Log.VERBOSE,null,message,args);
    }

    public static void debug() {
        println(Log.DEBUG,null,null,null);
    }

    public static void debug(String message, Object... args) {
        println(Log.DEBUG,null,message,args);
    }

    public static void info() {
        println(Log.INFO,null,null,null);
    }

    public static void info(String message, Object... args) {
        println(Log.INFO,null,message,args);
    }

    public static void warn() {
        println(Log.WARN,null,null,null);
    }

    public static void warn(String message, Object... args) {
        println(Log.WARN,null,message,args);
    }

    public static void warn(Throwable throwable) {
        println(Log.WARN,throwable,null,null);
    }

    public static void warn(Throwable throwable,String message,Object... args) {
        println(Log.WARN,throwable,message,args);
    }

    public static void error(String message, Object... args) {
        println(Log.ERROR,null,message,args);
    }

    public static void error(Throwable throwable) {
        println(Log.ERROR,throwable,null,null);
    }

    public static void error(Throwable throwable, String message, Object... args) {
        println(Log.ERROR,throwable,message,args);
    }

    protected static void println(int level,Throwable throwable,String message,Object... args) {
        if (level < LOG_PRIORITY) return;

        String method = getMethodName();
        LogInfo logInfo = new LogInfo(level,method,message,args,throwable);
        if (PRINT_PLUGIN != null) {
            String finalPrintMessage= PRINT_PLUGIN.toString(logInfo);
            Log.println(level,GLOBAL_TAG,finalPrintMessage);
        }
    }

    protected static String getMethodName() {
        Throwable throwable = new Throwable();
        StackTraceElement element = throwable.getStackTrace()[3];
        return element.getClassName() + "#" + element.getMethodName();
    }

    public static class DefaultPrintPlugin implements PrintPlugin {

        @Override
        public String toString(LogInfo logInfo) {
            String message = " ";
            if (logInfo.message != null) {
                message = logInfo.message;
                if (logInfo.args != null) {
                    message = String.format(logInfo.message,logInfo.args);
                }
            }

            String method = logInfo.method;
            String[] methodNames = method.split("#");
            if (methodNames != null && methodNames.length > 1) {
                String simpleClassName = getSimpleName(methodNames[0]);
                method = simpleClassName + "#" + methodNames[1];
            }
            message = String.format("[%s] %s",method,message);

            if (logInfo.throwable != null) {
                message += "\n " + Log.getStackTraceString(logInfo.throwable);
            }

            return message;
        }

        private static String getSimpleName(String className) {
            int lastIndex = className.lastIndexOf(".");
            if (lastIndex == -1) {
                return className;
            } else {
                return className.substring(lastIndex+1);
            }
        }
    }

    public static class LogInfo {
        public int level;
        public String message;
        public Object[] args;
        public String method;
        public Throwable throwable;

        public LogInfo(int level, String method,
                       String message, Object[] args,
                       Throwable throwable) {
            this.level = level;
            this.method = method;
            this.message = message;
            this.args = args;
            this.throwable = throwable;
        }
    }

    public static interface PrintPlugin {
        public String toString(LogInfo logInfo);
    }

}
