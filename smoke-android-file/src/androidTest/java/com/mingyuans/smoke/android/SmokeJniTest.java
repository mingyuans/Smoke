package com.mingyuans.smoke.android;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.mingyuans.smoke.Processes;
import com.mingyuans.smoke.SubSmoke;
import com.mingyuans.smoke.android.file.AndroidFilePrinter;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

/**
 * Created by yanxq on 2017/2/23.
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class SmokeJniTest {

    @Test
    public void testAppenderOpen() {
        Processes processes = AndroidProcesses.androidDefault();
        processes.addLast(new AndroidFilePrinter());
        SubSmoke subSmoke = new SubSmoke("Smoke",processes);
        subSmoke.open();

        try {
            Thread.sleep(3 *1000);
        } catch (InterruptedException e) {
            subSmoke.error(e);
        }

        subSmoke.info("hello,Smoke!");
        subSmoke.debug("hello,Smoke!");
    }

}
