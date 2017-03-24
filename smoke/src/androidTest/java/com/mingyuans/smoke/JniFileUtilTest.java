package com.mingyuans.smoke;

import android.content.Context;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Created by yanxq on 17/2/17.
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class JniFileUtilTest extends BaseTestUnit{

    static {
        System.loadLibrary("smoke-test-lib");
    }

    @BeforeClass
    public static void beforeClass() {
        Smoke.install(InstrumentationRegistry.getContext(),"Smoke");
    }


    @Test
    public void testIsDir() {
        File sdcardDirectory = Environment.getExternalStorageDirectory();
        File testDir = new File(sdcardDirectory,"smoke_test_dir");
        Smoke.debug("test dir path : %s",testDir.getAbsolutePath());

        if (testDir.exists()) {
            testDir.delete();
        }
        assertFalse(is_directory(testDir.getAbsolutePath()));

        testDir.mkdir();
        assertTrue(is_directory(testDir.getAbsolutePath()));

        testDir.delete();
    }

    @Test
    public void testDeleteDir() throws Exception {
        File sdcardDir = Environment.getExternalStorageDirectory();
        File testDir = new File(sdcardDir,"smoke_test_dir");

        if (!testDir.exists()) {
            testDir.mkdirs();
        }
        assertTrue(delete_dir(testDir.getAbsolutePath()));

        testDir.mkdirs();
        File testChildFile = new File(testDir,"test.txt");
        testChildFile.createNewFile();
        File testChildDir = new File(testDir,"child");
        testChildDir.mkdirs();

        assertTrue(testDir.exists());
        assertTrue(delete_dir(testDir.getAbsolutePath()));
        assertFalse(testDir.exists());
    }

    @Test
    public void testMakeDir() {
        File sdcardDir = Environment.getExternalStorageDirectory();
        File testDir = new File(sdcardDir,"smoke");

        if (testDir.exists()) {
            testDir.delete();
        }

        assertFalse(testDir.exists());
        assertTrue(mk_dir(testDir.getAbsolutePath()));
        assertTrue(testDir.exists());

        assertTrue(mk_dir(testDir.getAbsolutePath()));
        assertTrue(testDir.exists());

        testDir.delete();
        File childDir = new File(testDir,"child");
        assertFalse(childDir.exists());

        assertTrue(mk_dir(childDir.getAbsolutePath()));
        assertTrue(childDir.exists());
    }

    @Test
    public void testLastModifyTime() throws Exception {
        File sdcardDir = Environment.getExternalStorageDirectory();
        File testFile = new File(sdcardDir,"smokeTestFile.txt");
        if (!testFile.exists()) {
            testFile.createNewFile();
        }

        long actualLastModified = testFile.lastModified() / 1000;
        long lastModifyTime = last_modify_time(testFile.getAbsolutePath());
        assertEquals(actualLastModified,lastModifyTime);
    }

    @Test
    public void testFindChildFiles() throws Exception {
        File sdcardDir = Environment.getExternalStorageDirectory();
        File testDir = new File(sdcardDir,"smoke_test_dir");

        if (!testDir.exists()) {
            testDir.mkdirs();
        }
        assertTrue(delete_dir(testDir.getAbsolutePath()));

        testDir.mkdirs();
        File testChildFile = new File(testDir,"test.txt");
        testChildFile.createNewFile();
        File testChildDir = new File(testDir,"child");
        testChildDir.mkdirs();

        String[] child_array = find_dir_files(testDir.getAbsolutePath());
        assertTrue(child_array != null);
        assertEquals(2,child_array.length);
        Smoke.info("child : %s", Arrays.toString(child_array));
    }

    @Test
    public void testNativeCrashCatch() throws Exception {
        make_native_crash();
        Thread.sleep(10 * 1000);
    }

    public native void make_native_crash();

    public native boolean is_directory(String filePath);

    public native boolean mk_dir(String filePath);

    public native boolean delete_dir(String filePath);

    public native long last_modify_time(String filePath);

    public native String[] find_dir_files(String filePath);

}
