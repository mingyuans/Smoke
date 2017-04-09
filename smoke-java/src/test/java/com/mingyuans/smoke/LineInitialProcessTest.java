package com.mingyuans.smoke;

import org.junit.Test;


import static org.junit.Assert.*;

/**
 * Created by yanxq on 2017/4/9.
 */
public class LineInitialProcessTest {

    @Test
    public void testInProguard() throws Exception {
        LineInitialProcess process = new LineInitialProcess(true);
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        StackTraceElement element = elements[1];
        String methodString = process.getMethodString(element);
        assertTrue(methodString != null && methodString.length() > 0);
        assertTrue(methodString.startsWith(this.getClass().getName()));
        System.out.println(methodString);
    }

    @Test
    public void testNotInProguard() throws Exception {
        LineInitialProcess process = new LineInitialProcess();
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        StackTraceElement element = elements[1];
        String methodString = process.getMethodString(element);
        assertTrue(methodString != null && methodString.length() > 0);
        assertTrue(methodString.startsWith(this.getClass().getSimpleName()));
        System.out.println(methodString);
    }


}