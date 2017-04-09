package com.mingyuans.smoke;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by yanxq on 2017/3/15.
 */
public class LineInitialProcess extends Smoke.Process {

    private boolean isProguard = false;

    public LineInitialProcess() {
        String selfSimpleClassName = LineInitialProcess.class.getSimpleName();
        isProguard = selfSimpleClassName.length() <= 3;
    }

    public LineInitialProcess(boolean isProguard) {
        this.isProguard = isProguard;
    }

    @Override
    public boolean proceed(Smoke.LogBean logBean, List<String> messages, Chain chain) {
        if (messages == null) {
            messages = new LinkedList<>();
        }
        messages.add(generateHeadMessage(logBean));
        //content line must be added even if empty!
        messages.add(generateContentMessage(logBean));
        String throwableLine = generateThrowableMessage(logBean);
        if (!StringUtil.isEmpty(throwableLine)) {
            messages.add(throwableLine);
        }
        return chain.proceed(logBean,messages);
    }

    protected String generateHeadMessage(Smoke.LogBean logBean) {
        StringBuilder builder = new StringBuilder();
        if (logBean.subList != null && logBean.subList.size() > 0) {
            List<String> subList = logBean.subList;
//            int startIndex = subList.get(0).equals(logBean.tag)? 1 : 0;
            int startIndex = 0;
            if (subList.size() > startIndex) {
                builder.append("【");
                for (int i = startIndex, size = subList.size(); i < size; i++) {
                    if (i != startIndex) {
                        builder.append("|");
                    }
                    builder.append(subList.get(i));
                }
                builder.append("】");
            }
        }

        String methodLine = getMethodString(logBean.traceElement);
        builder.append("[" + methodLine + "]");

        String thread = logBean.thread == null? "unknown" : logBean.thread;
        builder.append("[thread: " + thread + "]");
        return builder.toString();
    }

    protected String generateContentMessage(Smoke.LogBean logBean) {
        String message = "";
        if (logBean.message != null) {
            message = logBean.message;
            if (logBean.args != null && logBean.args.length > 0) {
                try {
                    message = StringUtil.format(logBean.message, logBean.args);
                } catch (Throwable throwable) {
                    message = logBean.message + " ==>[format error]\n" + throwable.getMessage();
                }
            }
        }
        return message;
    }

    protected String generateThrowableMessage(Smoke.LogBean logBean) {
        return logBean.throwable == null? "" : StringUtil.throwable2String(logBean.throwable);
    }

    protected String getMethodString(StackTraceElement traceElement) {
        if (traceElement == null) {
            return "null";
        }

        StringBuilder methodBuilder = new StringBuilder();
        String className = isProguard ? traceElement.getClassName()
                : StringUtil.getSimpleName(traceElement.getClassName());
        methodBuilder.append(className);
        methodBuilder.append(".");
        methodBuilder.append(traceElement.getMethodName());
        if (!isProguard
                && !StringUtil.isEmpty(traceElement.getFileName())
                && traceElement.getLineNumber() > 0) {
            methodBuilder.append("(");
            methodBuilder.append(traceElement.getFileName());
            methodBuilder.append(":");
            methodBuilder.append(traceElement.getLineNumber());
            methodBuilder.append(")");
        }
        return methodBuilder.toString();
    }
}
