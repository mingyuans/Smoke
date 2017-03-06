package com.mingyuans.smoke;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;


import static org.junit.Assert.*;

/**
 * Created by yanxq on 17/2/15.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class SmokeTest {

    @BeforeClass
    public static void beforeClass() throws Exception {
        Smoke.install("Smoke",null);
    }

    @Before
    public void before() throws Exception {

    }

    @Test
    public void testDebug() {
        Smoke.debug("");
        Smoke.debug("message: %s","hello");
        Smoke.debug(new Throwable());
        assertNotNull(new Object());
    }

    @Test
    public void testWarn() {
        Smoke.info();
        Smoke.info("message: %s","hello");
        assertNotNull(new Object());
    }

    @Test
    public void testVerbose() {
        Smoke.verbose();
        Smoke.verbose("message: %s","hello");
        assertNotNull(new Object());
    }

    @Test
    public void testError() {
        Smoke.error("message: %s","test error.");
        Throwable throwable = new Throwable();
        Smoke.error(throwable);
        Smoke.error(throwable,"message: %s","test error with throwable");

        Throwable throwable1 = new Throwable();
        throwable.initCause(throwable1);
        Smoke.error(throwable);
    }

    @Test
    public void testPriorityFilter() {
        Smoke.setLogPriority(Log.INFO);
        Smoke.debug("test priority error!");
        Smoke.info("test priority success.");
        Smoke.setLogPriority(Log.VERBOSE);
        assertNotNull(new Object());
    }

    @Test
    public void testErrorFormat() {
        Smoke.info("test formart : %d","hello");
        assertTrue(true);
    }

    @Test
    public void testFormat() {
        String[] array = new String[]{"Hello","World"};
        Smoke.debug("array is : {0}",(Object)array);
        Smoke.debug("array is %% : %s",(Object) array);
    }

    @Test
    public void testTooLongMessage() {
        final int MAX_LINE_LENGTH = 4000;
        StringBuilder builder = new StringBuilder();
        int index = 0;
        while (builder.length() < (MAX_LINE_LENGTH + 200)) {
            builder.append("[" + index++ + "]");
        }
        Smoke.debug("last index : " + (index-1));
        Smoke.debug(builder.toString());
    }

    @Test
    public void testJson() throws JSONException {
        String str = "{\"content\":\"this is the msg content.\",\"tousers\":\"user1|user2\",\"msgtype\":\"texturl\",\"appkey\":\"test\",\"domain\":\"test\","
                + "\"system\":{\"wechat\":{\"safe\":\"1\"}},\"texturl\":{\"urltype\":\"0\",\"user1\":{\"spStatus\":\"user01\",\"workid\":\"work01\"},\"user2\":{\"spStatus\":\"user02\",\"workid\":\"work02\"}}}";
        Smoke.debug(str);

        Smoke.debug("json content : {0}, suffix: {1}",str,"json_end");
    }

}