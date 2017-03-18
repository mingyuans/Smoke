package com.mingyuans.smoke;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.json.JSONException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;


import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static org.junit.Assert.*;

/**
 * Created by yanxq on 17/2/15.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class SmokeTest {

    @BeforeClass
    public static void beforeClass() throws Exception {
        Smoke.install(InstrumentationRegistry.getContext(),"Smoke");
    }

    @Before
    public void before() throws Exception {

    }

    @Test
    public void testDebug() {
        Smoke.debug("");
        Smoke.debug("debug message: %s","hello");
        Smoke.debug(new Throwable());
        assertNotNull(new Object());
    }

    @Test
    public void testWarn() {
        Smoke.warn();
        Smoke.warn("warn message: %s","hello");
        assertNotNull(new Object());
    }

    @Test
    public void testVerbose() {
        Smoke.verbose();
        Smoke.verbose("verbose message: %s","hello");
        assertNotNull(new Object());
    }

    @Test
    public void testInfo() {
        Smoke.info();
        Smoke.info("info message: %s","hello");
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

        Smoke.json(Smoke.DEBUG,str);

        Smoke.debug("json content : {0}, suffix: {1}",str,"json_end");
    }

    @Test
    public void testXml() {
        String xmlString = "<team><member name=\"Elvis\"/><member name=\"Leon\"/></team>";
        Smoke.xml(Smoke.DEBUG,xmlString);

        String errorXml = "error xml";
        Smoke.xml(Smoke.DEBUG,errorXml);

        Smoke.debug("Show xml content: \n{0}",xmlString);
    }

    @Test
    public void testLog() {
        Smoke.log(Smoke.DEBUG,"SmokeTest","This is Smoke log method. {0}","hello");
    }

    @Test
    public void testNewSub() {
        SmokeSub smokeSub = Smoke.newSub("subOne");
        smokeSub.debug("Hello, subOne!");
        SmokeSub smokeSub1 = smokeSub.newSub("subTwo");
        smokeSub1.debug("Hello, {0}!","subTwo");
    }

    @Test
    public void testLongMessage() throws Exception {
        OkHttpClient client = new OkHttpClient.Builder()
                .build();
        Request request = new Request.Builder()
                .url("https://github.com/mingyuans/Smoke")
                .build();
        Response response = client.newCall(request).execute();
        String responseMessage = response.body().string();
        response.close();
        Smoke.info(responseMessage);
    }

    @Test
    public void testAttachPrinter() throws Exception {
        SmokeSub smokeSub = new SmokeSub(InstrumentationRegistry.getContext(),"Smoke",null);
        smokeSub.attach(new Printer() {
            @Override
            public void println(int priority, String tag, String message) {
                Log.println(priority,tag,"Smoke Printer: Hello");
                Log.println(priority,tag,message);
            }
        });
        smokeSub.debug("Hello, Printer!");
    }

    @Test
    public void testAttachSub() throws Exception {
        SmokeSub smokeSub = new SmokeSub(InstrumentationRegistry.getContext(),"SmokeParent",null);
        smokeSub.getProcesses().addCollector(new Smoke.Process() {
            @Override
            public boolean proceed(Smoke.LogBean logBean, List<String> messages, Chain chain) {
                messages.add(0,"Smoke A: Hello, Smoke B.");
                return chain.proceed(logBean,messages);
            }
        });
        SmokeSub smokeSub1 = new SmokeSub(InstrumentationRegistry.getContext(),"smokeChild",null);
        smokeSub1.attach(smokeSub);
        smokeSub1.info("Hello,Smoke.");
    }

    @Test
    public void testClose() throws Exception {
        final AtomicInteger atomicInteger = new AtomicInteger(2);
        SmokeSub smokeSub = new SmokeSub(InstrumentationRegistry.getContext(),"Smoke",null);
        smokeSub.getProcesses()
                .addPrinter(new Smoke.Process() {
                    @Override
                    public boolean proceed(Smoke.LogBean logBean, List<String> messages, Chain chain) {
                        return chain.proceed(logBean,messages);
                    }

                    @Override
                    public void close() {
                        assertEquals(0,atomicInteger.get());
                    }
                });
        SmokeSub smokeSub1 = smokeSub.newSub("SmokeSub1");
        SmokeSub smokeSub2 = smokeSub1.newSub("SmokeSub2");
        smokeSub2.close();
        atomicInteger.getAndSet(1);
        smokeSub1.close();
        atomicInteger.getAndSet(0);
        smokeSub.close();

    }


}