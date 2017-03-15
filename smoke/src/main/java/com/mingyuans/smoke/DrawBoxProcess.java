package com.mingyuans.smoke;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by yanxq on 2017/3/15.
 */

public class DrawBoxProcess extends Smoke.Process {
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
    public boolean proceed(Smoke.LogBean logBean, List<String> messages, Chain chain) {
        List<String> newLines = messages;
        if (!CollectionUtil.isEmpty(messages)) {
            newLines = new LinkedList<>();
            newLines.add(TOP_BORDER);
            for (int i = 0; i < messages.size(); i++) {
                String line = messages.get(i);
                if (line == null)  {
                    continue;
                }
                String[] lines = makeSureBelowMaxPrintLength(line);
                for (String contentLine : lines) {
                    newLines.add(wrapperHorizontalLine(contentLine));
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
        String[] lines = message.split("\n");
        StringBuilder messageBuilder = new StringBuilder();
        for (String line : lines) {
            messageBuilder.append(HORIZONTAL_DOUBLE_LINE + line + "\n");
        }
        return messageBuilder.toString();
    }

    protected String[] makeSureBelowMaxPrintLength(String message) {
        LinkedList<String> newMessageLines = new LinkedList<String>();
        if (message.length() > MAX_LINE_LENGTH) {
            int splits = message.length() / MAX_LINE_LENGTH + 1;
            for (int i = 0, startIndex = 0; i < splits; i++) {
                int endIndex = startIndex + MAX_LINE_LENGTH > message.length()?
                        message.length() : startIndex + MAX_LINE_LENGTH;
                String lineText = message.substring(startIndex,endIndex);
                newMessageLines.add(lineText);
                startIndex = endIndex;
            }
        } else {
            newMessageLines.add(message);
        }
        return newMessageLines.toArray(new String[newMessageLines.size()]);
    }
}
