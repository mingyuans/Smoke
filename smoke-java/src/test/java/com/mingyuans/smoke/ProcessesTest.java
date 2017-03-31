package com.mingyuans.smoke;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by yanxq on 17/3/31.
 */
public class ProcessesTest {
    @Test
    public void testAdd() throws Exception {
        Processes processes = new Processes();
        processes.add("test_1",new TestProcess("test_1"));
        assertNotNull(processes.getByIdentify("test_1"));
        assertNotNull(processes.get(0));
        assertEquals(1,processes.size());

        processes.addFirst("test_0",new TestProcess("test_0"));
        TestProcess process = (TestProcess) processes.get(0);
        assertEquals("test_0",process.getName());
    }

    @Test
    public void testRemove() throws Exception {
        Processes processes = new Processes();
        processes.add("test_1",new TestProcess("test_1"));
        processes.removeByIdentify("test_1");
        assertEquals(0,processes.size());
    }

    @Test
    public void testInsert() throws Exception {
        Processes processes = new Processes();
        processes.add("test_1",new TestProcess("test_1"));
        processes.insertBefore("test_1","test_0",new TestProcess("test_0"));
        processes.insertAfter("test_1","test_2",new TestProcess("test_2"));
        TestProcess before = (TestProcess) processes.get(0);
        assertEquals("test_0",before.getName());
        TestProcess after = (TestProcess) processes.get(2);
        assertEquals("test_2",after.getName());
    }

    private static class TestProcess extends Smoke.Process {
        private String name = "";
        public TestProcess(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean proceed(Smoke.LogBean logBean, List<String> messages, Chain chain) {
            return false;
        }
    }
}