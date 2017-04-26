package com.mingyuans.smoke;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by yanxq on 2017/3/15.
 */

public class DrawBoxProcess extends Smoke.Process {
    public static final String CR = "\r\n";
    public static final char TOP_LEFT_CORNER = '╔';
    public static final char BOTTOM_LEFT_CORNER = '╚';
    public static final char MIDDLE_CORNER = '╟';
    public static final char HORIZONTAL_DOUBLE_LINE = '║';
    public static final String DOUBLE_DIVIDER = "════════════════════════════════════════════";
    public static final String SINGLE_DIVIDER = "────────────────────────────────────────────";
    public static final String TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    public static final String BOTTOM_BORDER = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    public static final String MIDDLE_BORDER = MIDDLE_CORNER + SINGLE_DIVIDER + SINGLE_DIVIDER;

    private static final int DEFAULT_LINE_MAX_LENGTH = 6000;

    private final int mLineMaxLength;

    public DrawBoxProcess() {
        mLineMaxLength = DEFAULT_LINE_MAX_LENGTH;
    }

    public DrawBoxProcess(int lineMaxLength) {
        mLineMaxLength = lineMaxLength - 2;
    }

    @Override
    public boolean proceed(Smoke.LogBean logBean, List<String> messages, Chain chain) {
        List<String> newLines = messages;
        if (!CollectionUtil.isEmpty(messages)) {
            newLines = new LinkedList<>();
            newLines.add(TOP_BORDER);
            for (int i = 0,size = messages.size(); i < size; i++) {
                String line = messages.get(i);
                if (line == null)  {
                    continue;
                }
                String[] splits = makeSureBelowMaxPrintLength(line);
                for (String splitLine : splits) {
                    newLines.add(wrapperHorizontalLine(splitLine));
                }
                if (i != messages.size() - 1) {
                    newLines.add(MIDDLE_BORDER);
                }
            }
            newLines.add(BOTTOM_BORDER);
        }
        return chain.proceed(logBean,newLines);
    }

    protected String wrapperHorizontalLine(String message) {
        return HORIZONTAL_DOUBLE_LINE + message + "\n";
    }

    protected String[] makeSureBelowMaxPrintLength(String message) {
        LinkedList<String> messageLines = new LinkedList<>();
        StringTokenizer stringTokenizer = new StringTokenizer(message,System.getProperty("line.separator"));
        while (stringTokenizer.hasMoreTokens()) {
            String oneLine = stringTokenizer.nextToken();
            int length = oneLine.length();
            if (length > mLineMaxLength) {
                int splits = length / mLineMaxLength + 1;
                for (int i = 0, startIndex = 0; i < splits; i++) {
                    int endIndex = startIndex + mLineMaxLength > oneLine.length() ?
                            oneLine.length() : startIndex + mLineMaxLength;
                    String line = oneLine.substring(startIndex, endIndex);
                    messageLines.add(line);
                    startIndex = endIndex;
                }
            } else {
                messageLines.add(oneLine);
            }
        }
        return messageLines.toArray(new String[messageLines.size()]);
    }
}
