package com.mingyuans.smoke;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by yanxiangqun on 2017/4/26.
 */
public class DrawBoxProcessTest {

    @Test
    public void testLimitLogLength() throws Exception {
        DrawBoxProcess drawBoxProcess = new DrawBoxProcess(3900);
        LinkedList<String> messages = new LinkedList<>();
        messages.add("[SmokeTest.doSmokePrint(SmokeTest.java:59)][thread: main]");
        StringBuilder builder = new StringBuilder();
        while (builder.length() < 4400) {
            builder.append(builder.length() + ",");
            if (builder.length() == 400) {
                builder.append("\n");
            }
        }
        messages.add(builder.toString());

        Processes processes = new Processes();
        processes.clear();
        processes.add(new Smoke.Process() {
            @Override
            public boolean proceed(Smoke.LogBean logBean, List<String> messages, Chain chain) {
                for (String message : messages) {
//                    System.out.println("length : " + message.length());
                    System.out.println(message);
                    assertTrue(message.length() <= 3900);
                }
                return false;
            }
        });

        drawBoxProcess.proceed(null,messages,new Smoke.Process.Chain(0,processes));
    }

}