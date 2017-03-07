package com.mingyuans.smoke;

import android.text.TextUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;

/**
 * Created by yanxq on 2017/3/2.
 */

public class DefaultPrintPlugin implements Smoke.PrintPlugin {

    protected static final int MAX_LINE_LENGTH = 3990;

    protected static final char TOP_LEFT_CORNER = '╔';
    protected static final char BOTTOM_LEFT_CORNER = '╚';
    protected static final char MIDDLE_CORNER = '╟';
    protected static final char HORIZONTAL_DOUBLE_LINE = '║';
    protected static final String DOUBLE_DIVIDER = "════════════════════════════════════════════";
    protected static final String SINGLE_DIVIDER = "────────────────────────────────────────────";
    protected static final String TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    protected static final String BOTTOM_BORDER = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    protected static final String MIDDLE_BORDER = MIDDLE_CORNER + SINGLE_DIVIDER + SINGLE_DIVIDER;

    @Override
    public String[] toString(Smoke.LogInfo logInfo) {
        if (logInfo == null) {
            return new String[]{""};
        }

        LinkedList<String> messages = new LinkedList<String>();
        messages.addFirst(TOP_BORDER);

        String headMessage = generateHeadMessage(logInfo);
        if (!TextUtils.isEmpty(headMessage)) {
            messages.add(HORIZONTAL_DOUBLE_LINE + headMessage);
        }

        String contentMessage = generateContentMessage(logInfo);
        if (contentMessage != null) {
            messages.add(MIDDLE_BORDER);
            String[] contentLines = makeSureBelowMaxPrintLength(contentMessage);
            for (String contentLine : contentLines) {
                messages.add(wrapperHorizontalLine(contentLine));
            }
        }

        String tracesMessage = generateThrowableMessage(logInfo);
        if (!TextUtils.isEmpty(tracesMessage)) {
            messages.add(MIDDLE_BORDER);
            messages.add(wrapperHorizontalLine(tracesMessage));
        }

        messages.add(BOTTOM_BORDER);
        return messages.toArray(new String[messages.size()]);
    }

    protected String wrapperHorizontalLine(String message) {
        String[] lines = message.split("\n");
        StringBuilder messageBuilder = new StringBuilder();
        for (String line : lines) {
            messageBuilder.append(HORIZONTAL_DOUBLE_LINE + line + "\n");
        }
        return messageBuilder.toString();
    }

    protected String generateHeadMessage(Smoke.LogInfo logInfo) {
        StringBuilder builder = new StringBuilder();
        if (logInfo.subTags != null && logInfo.subTags.size() > 1) {
            for (int i = 1, size = logInfo.subTags.size(); i < size; i++) {
                builder.append("【" + logInfo.subTags.get(i) + "】");
            }
            builder.append(" ");
        }

        String methodLine = getMethodString(logInfo.traceElement);
        builder.append("[" + methodLine + "]");

        String thread = logInfo.thread == null? "unknown" : logInfo.thread;
        builder.append("[tn: " + thread + "]");
        return builder.toString();
    }

    protected String generateContentMessage(Smoke.LogInfo logInfo) {
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
        return message;
    }

    protected String[] makeSureBelowMaxPrintLength(String message) {
        LinkedList<String> finalMessageLines = new LinkedList<String>();
        if (message.length() > MAX_LINE_LENGTH) {
            int splits = message.length() / MAX_LINE_LENGTH + 1;
            for (int i = 0, startIndex = 0; i < splits; i++) {
                int endIndex = startIndex + MAX_LINE_LENGTH > message.length()?
                        message.length() : startIndex + MAX_LINE_LENGTH;
                String lineText = message.substring(startIndex,endIndex);
                finalMessageLines.add(lineText);
                startIndex = startIndex + endIndex;
            }
        } else {
            finalMessageLines.add(message);
        }
        return finalMessageLines.toArray(new String[finalMessageLines.size()]);
    }

    protected String generateThrowableMessage(Smoke.LogInfo logInfo) {
        return logInfo.throwable == null? "" : getStackTraceString(logInfo.throwable);
    }

    protected String getMethodString(StackTraceElement traceElement) {
        if (traceElement == null) {
            return "null";
        }

        StringBuilder methodBuilder = new StringBuilder();
        String simpleClass = StringUtil.getSimpleName(traceElement.getClassName());
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
