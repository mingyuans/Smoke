package com.mingyuans.smoke.android;

import android.support.test.runner.AndroidJUnit4;

import com.mingyuans.smoke.Smoke;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by yanxq on 2017/4/4.
 */
@RunWith(AndroidJUnit4.class)
public class AndroidSmokeTest {

    @BeforeClass
    public static void beforeClass() throws Exception {
        Smoke.install("Smoke",AndroidProcesses.androidDefault());
    }

    @Test
    public void testDebugPrint() throws Exception {
        Smoke.debug();

        Smoke.debug("Hello");
    }

    @Test
    public void testLongEnterLog() throws Exception {
        StringBuilder builder = new StringBuilder();
        int number = 0;
        while (builder.length() < 5000) {
            builder.append(number++ + "Hello,Smoke;\n");
        }
        Smoke.debug(builder.toString());
    }

}
