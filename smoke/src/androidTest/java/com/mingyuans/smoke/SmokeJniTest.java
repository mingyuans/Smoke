package com.mingyuans.smoke;

import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import static org.junit.Assert.*;

/**
 * Created by yanxq on 2017/2/23.
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class SmokeJniTest extends BaseTestUnit {

    @Test
    public void testAppenderOpen() {
        File storageDir = Environment.getExternalStorageDirectory();
        File smokeDir = new File(storageDir,"smoke");

        File cacheDir = InstrumentationRegistry.getContext().getExternalFilesDir("smoke");
        SubSmoke subSmoke = new SubSmoke(InstrumentationRegistry.getContext(),"Smoke",null);
        assertTrue(true);
        subSmoke.info("hello,Smoke!");
        subSmoke.debug("hello,Smoke!");

        try {
            Thread.sleep(3* 60 * 1000);
        } catch (InterruptedException e) {
            subSmoke.error(e);
        }
    }

//    public native void appender_open(String dirPath, String cacheDir,String namePrefix);

}
