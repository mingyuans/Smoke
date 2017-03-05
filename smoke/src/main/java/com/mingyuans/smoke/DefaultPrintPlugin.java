package com.mingyuans.smoke;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;

/**
 * Created by yanxq on 2017/3/2.
 */

public class DefaultPrintPlugin implements Smoke.PrintPlugin {

    @Override
    public String toString(Smoke.LogInfo logInfo) {
        if (logInfo == null) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        if (logInfo.subTags != null) {
            for (int i = 1, size = logInfo.subTags.size(); i < size; i++) {
                builder.append("【" + logInfo.subTags.get(i) + "】");
            }
            builder.append(" ");
        }

        String message = " ";
        if (logInfo.message != null) {
            message = logInfo.message;
            if (logInfo.args != null && logInfo.args.length > 0) {
                try {
                    message = String.format(logInfo.message,logInfo.args);
                } catch (Throwable throwable) {
                    message = logInfo.message + " ==>[format error]\n" + throwable.getMessage();
                }
            }
        }

        String method = logInfo.method;
        String[] methodNames = method.split("#");
        if (methodNames != null && methodNames.length > 1) {
            String simpleClassName = getSimpleName(methodNames[0]);
            method = simpleClassName + "#" + methodNames[1];
        }


        String thread = logInfo.thread == null? "unknown" : logInfo.thread;
        message = String.format("[%s][tn=%s] %s",method,thread,message);
        builder.append(message);

        if (logInfo.throwable != null) {
            builder.append("\n ");
            builder.append(getStackTraceString(logInfo.throwable));
        }

        return builder.toString();
    }

    private static String getSimpleName(String className) {
        int lastIndex = className.lastIndexOf(".");
        if (lastIndex == -1) {
            return className;
        } else {
            return className.substring(lastIndex+1);
        }
    }

    private static String getStackTraceString(Throwable throwable) {
        if (throwable == null) {
            return "";
        }

        Throwable cause = throwable;
        while (cause != null) {
            cause = cause.getCause();
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }
}
