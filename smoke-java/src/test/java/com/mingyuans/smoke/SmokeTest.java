package com.mingyuans.smoke;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by yanxq on 17/2/15.
 */
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
        Smoke.debug("debug message: %s","hello");
        Smoke.debug(new Throwable());
        assertNotNull(new Object());
        Smoke.debug("hello,{}","LiLei");
    }

    @Test
    public void testWarn() {
        Smoke.warn();
        Smoke.warn("warn message: %s","hello");
        assertNotNull(new Object());

        SubSmoke subSmoke = new SubSmoke("Smoke");
        subSmoke.warn("hello");
        Throwable throwable = new Exception();
        subSmoke.warn(throwable);
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
        Smoke.setLogPriority(Smoke.INFO);
        Smoke.debug("test priority error!");
        Smoke.info("test priority success.");
        Smoke.setLogPriority(Smoke.VERBOSE);
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
    public void testJson() {
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
        SubSmoke subSmoke = Smoke.newSub("subOne");
        subSmoke.debug("Hello, subOne!");
        SubSmoke subSmoke1 = subSmoke.newSub("subTwo");
        subSmoke1.debug("Hello, {0}!","subTwo");
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
        SubSmoke subSmoke = new SubSmoke("Smoke",null);
        subSmoke.attach(new Printer() {
            @Override
            public void println(int priority, String tag, String message) {
                System.out.println("Smoke Printer: Hello");
                System.out.println(message);
            }
        });
        subSmoke.debug("Hello, Printer!");
    }

    @Test
    public void testAttachSub() throws Exception {
        SubSmoke subSmoke = new SubSmoke("SmokeParent",null);
        subSmoke.getProcesses().addCollector(new Smoke.Process() {
            @Override
            public boolean proceed(Smoke.LogBean logBean, List<String> messages, Chain chain) {
                messages.add(0,"Smoke A: Hello, Smoke B.");
                return chain.proceed(logBean,messages);
            }
        });
        SubSmoke subSmoke1 = new SubSmoke("smokeChild",null);
        subSmoke1.attach(subSmoke);
        subSmoke1.info("Hello,Smoke.");
    }

    @Test
    public void testClose() throws Exception {
        final AtomicInteger atomicInteger = new AtomicInteger(2);
        SubSmoke subSmoke = new SubSmoke("Smoke",null);
        subSmoke.getProcesses()
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
        SubSmoke subSmoke1 = subSmoke.newSub("SmokeSub1");
        SubSmoke subSmoke2 = subSmoke1.newSub("SmokeSub2");
        subSmoke2.close();
        atomicInteger.getAndSet(1);
        subSmoke1.close();
        atomicInteger.getAndSet(0);
        subSmoke.close();

    }


}