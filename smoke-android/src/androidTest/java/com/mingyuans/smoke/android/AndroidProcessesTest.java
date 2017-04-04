package com.mingyuans.smoke.android;

import android.app.Application;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by yanxq on 2017/4/4.
 */
public class AndroidProcessesTest {
    @Test
    public void getApplication() throws Exception {
        Application application = AndroidProcesses.getApplication();
        assertTrue(application != null);
    }

}