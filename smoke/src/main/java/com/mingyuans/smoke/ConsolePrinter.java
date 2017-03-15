package com.mingyuans.smoke;

import android.util.Log;

import java.util.List;

/**
 * Created by yanxq on 17/3/15.
 */

public class ConsolePrinter extends Smoke.Process{
    private static final int MAX_LINE_LENGTH = 4000;

    @Override
    public boolean proceed(Smoke.LogBean logBean, List<String> messages, Chain chain) {
        if (CollectionUtil.isEmpty(messages)) {
            return true;
        }

        for (String line : messages) {
            //检查是否过长
            if (line.length() > MAX_LINE_LENGTH) {
                int splits = line.length() / MAX_LINE_LENGTH + 1;
                for (int i = 0, startIndex = 0; i < splits; i++) {
                    int endIndex = startIndex + MAX_LINE_LENGTH > line.length()?
                            line.length(): startIndex + MAX_LINE_LENGTH;
                    String lineText = (startIndex == 0? "" : "  ") + line.substring(startIndex,endIndex);
                    Log.println(logBean.level,logBean.tag,lineText);
                    startIndex = endIndex;
                }
            } else {
                Log.println(logBean.level,logBean.tag,line);
            }
        }
        return chain.proceed(logBean,messages);
    }
}
