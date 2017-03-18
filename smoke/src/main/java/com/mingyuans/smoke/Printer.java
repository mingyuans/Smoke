package com.mingyuans.smoke;

import java.util.List;

/**
 * Created by yanxq on 2017/3/18.
 */

public interface Printer {
    public void println(int priority,String tag,String message);


    public static class PrinterProcess extends Smoke.Process{

        private Printer mPrinter;
        public PrinterProcess(Printer printer) {
            mPrinter = printer;
        }

        @Override
        public boolean proceed(Smoke.LogBean logBean, List<String> messages, Chain chain) {
            if (mPrinter != null) {
                StringBuilder builder = new StringBuilder();
                for (String line : messages) {
                    builder.append(line.endsWith("\n")? line : line + "\n");
                }
                mPrinter.println(logBean.level,logBean.tag,builder.toString());
            }
            return chain.proceed(logBean,messages);
        }
    }

}
