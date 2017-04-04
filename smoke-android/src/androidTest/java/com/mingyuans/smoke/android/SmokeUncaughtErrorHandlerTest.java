package com.mingyuans.smoke.android;

import android.support.test.runner.AndroidJUnit4;

import com.mingyuans.smoke.Smoke;
import com.mingyuans.smoke.SubSmoke;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

/**
 * Created by yanxq on 2017/3/12.
 */
@RunWith(AndroidJUnit4.class)
public class SmokeUncaughtErrorHandlerTest {

    @BeforeClass
    public static void beforeClass() throws Exception {
        Smoke.install("Smoke",AndroidProcesses.androidDefault());
    }

    @Test
    public void uncaughtException() throws Exception {
        Throwable throwable = new Throwable();
        SmokeUncaughtErrorHandler handler = new SmokeUncaughtErrorHandler(null);
        handler.uncaughtException(Thread.currentThread(),throwable);

        Smoke.error("In disable,and must not be printing!");

        handler.getPreferences().edit().clear().apply();
        SubSmoke.disable(false);
    }

    @Test
    public void isSmokeError() throws Exception {
        SmokeUncaughtErrorHandler handler = new SmokeUncaughtErrorHandler(null);
        Throwable throwable = new Throwable();
        assertTrue(handler.isSmokeError(throwable));
    }
}