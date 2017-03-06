package com.mingyuans.smoke;

import android.text.TextUtils;

import java.io.PrintWriter;
import java.io.StringWriter;

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

        String methodLine = getMethodString(logInfo.traceElement);
        builder.append("[" + methodLine + "]");

        String thread = logInfo.thread == null? "unknown" : logInfo.thread;
        builder.append("[tn: " + thread + "]");

        String message = "";
        if (logInfo.message != null) {
            message = logInfo.message;
            if (logInfo.args != null && logInfo.args.length > 0) {
                try {
                    message = StringUtil.format(logInfo.message,logInfo.args);
                } catch (Throwable throwable) {
                    message = logInfo.message + " ==>[format error]\n" + throwable.getMessage();
                }
            }
        }

        if (!TextUtils.isEmpty(message)) {
            builder.append(" \n  ");
            builder.append(message);
        }

        if (logInfo.throwable != null) {
            builder.append("\n ");
            builder.append(getStackTraceString(logInfo.throwable));
        }

        return builder.toString();
    }

    protected String getMethodString(StackTraceElement traceElement) {
        if (traceElement == null) {
            return "null";
        }

        StringBuilder methodBuilder = new StringBuilder();
        String simpleClass = getSimpleName(traceElement.getClassName());
        methodBuilder.append(simpleClass);
        methodBuilder.append("#");
        methodBuilder.append(traceElement.getMethodName());
        if (!TextUtils.isEmpty(traceElement.getFileName())
                && traceElement.getLineNumber() > 0) {
            methodBuilder.append("(");
            methodBuilder.append(traceElement.getFileName());
            methodBuilder.append(":");
            methodBuilder.append(traceElement.getLineNumber());
            methodBuilder.append(")");
        }
        return methodBuilder.toString();
    }

    protected String getSimpleName(String className) {
        int lastIndex = className.lastIndexOf(".");
        if (lastIndex == -1) {
            return className;
        } else {
            return className.substring(lastIndex+1);
        }
    }

    protected String getStackTraceString(Throwable throwable) {
        if (throwable == null) {
            return "";
        }

        Throwable lastCause = throwable;
        while (lastCause.getCause() != null) {
            lastCause = lastCause.getCause();
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        lastCause.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }


}
