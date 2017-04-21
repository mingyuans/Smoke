package com.mingyuans.smoke.android.file;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.mingyuans.smoke.Smoke;
import com.mingyuans.smoke.android.AndroidProcesses;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by yanxiangqun on 2017/4/21.
 */
public class AndroidFilePrinterTest {

    @BeforeClass
    public static void beforeClass() {
        Smoke.install("Smoke", AndroidProcesses.androidDefault());
    }

    @Test
    public void testGetLogFilePath() throws Exception {
        AndroidFilePrinter printer = new AndroidFilePrinter("test");
        printer.open();

        String logDirPath = printer.getCurrentLogDirPath();
        Context context = InstrumentationRegistry.getTargetContext();
        Smoke.info("log dir :{0}",logDirPath);
        File dirFile = new File(context.getExternalCacheDir(),"test");
        assertEquals(dirFile.getAbsolutePath(),logDirPath);


        String logFilePath = printer.getCurrentLogFilePath();
        Smoke.info("log file path: {0}",logFilePath);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String fileName = "test_" + dateFormat.format(new Date()) + ".sm";
        assertEquals(new File(dirFile,fileName).getAbsolutePath(),logFilePath);

        int beforeDays = 2;
        String[] files = printer.getLogFileFromTimespan(beforeDays);
        Smoke.info(files);
        assertEquals(2,files.length);

        Date today = new Date();
        Calendar calendar =Calendar.getInstance();
        calendar.setTime(today);
        calendar.set(Calendar.DATE,calendar.get(Calendar.DATE)-beforeDays);
        Date targetDate = calendar.getTime();
        String targetDateFileName = "test_" + dateFormat.format(targetDate) + ".sm";
        for (String file : files) {
            assertTrue(file.endsWith(targetDateFileName));
        }
    }


}