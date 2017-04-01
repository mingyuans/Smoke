package com.mingyuans.smoke;

/*****************************************************************************
 Copyright mingyuans

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ******************************************************************************/

import java.util.LinkedList;
import java.util.List;

/**
 * Created by yanxq on 2017/3/2.
 */

public class SubSmoke implements ISmoke {
    private static volatile boolean SMOKE_DISABLE = false;

    private volatile boolean isOpened= false;
    protected int mLogPriority = Smoke.VERBOSE;
    protected int mExtraMethodOffset = 0;
    protected Processes mProcesses;
    protected final LinkedList<String> mTags = new LinkedList<String>();

    public SubSmoke(String tag) {
        this(tag,null);
    }

    public SubSmoke(String tag, Processes processes) {
        if (!StringUtil.isEmpty(tag)) {
            mTags.addLast(tag);
        } else if (CollectionUtil.isEmpty(mTags)) {
            mTags.addFirst("Smoke");
        }

        if (processes == null) {
            processes = Processes.newDefault();
        }
        setProcesses(processes);
    }

    public static void disable(boolean disable) {
        SMOKE_DISABLE = disable;
    }

    private void event(String event,Object value) {
        if (mProcesses != null) {
            mProcesses.event(event, value);
        }
    }

    public void enableConsole(boolean consoleEnable) {
        event(ConsolePrinter.CONSOLE_ENABLE_B,consoleEnable);
    }

    public void attach(Printer printer) {
        if (printer != null) {
            mProcesses.addLast(new Printer.PrinterProcess(printer));
        }
    }

    public void attach(SubSmoke subSmoke) {
        mProcesses.clear();
        mProcesses.addFirst(new SubChainProcess(subSmoke));
    }

    public Processes getProcesses() {
        return mProcesses;
    }

    public void setProcesses(Processes processes) {
        if (mProcesses != processes) {
            mProcesses = processes;
            mProcesses.increaseReference();
        }
    }

    public void setExtraMethodOffset(int extraIndex) {
        mExtraMethodOffset = extraIndex;
    }

    public void setLogPriority(int priority) {
        mLogPriority = priority;
    }

    @Override
    public void verbose() {
        println(Smoke.VERBOSE,null,null);
    }

    @Override
    public void verbose(Object object) {
        String message = StringUtil.toString(object);
        println(Smoke.VERBOSE,null,message);
    }

    @Override
    public void verbose(String message, Object... args) {
        println(Smoke.VERBOSE,null,message,args);
    }

    @Override
    public void debug() {
        println(Smoke.DEBUG,null,null);
    }

    @Override
    public void debug(Object object) {
        if (object != null && object instanceof Throwable) {
            println(Smoke.DEBUG,(Throwable)object,null);
        } else {
            String message = StringUtil.toString(object);
            println(Smoke.DEBUG,null,message);
        }
    }

    @Override
    public void debug(String message, Object... args) {
        println(Smoke.DEBUG,null,message,args);
    }

    @Override
    public void info() {
        println(Smoke.INFO,null,null);
    }

    @Override
    public void info(Object object) {
        if (object != null && object instanceof Throwable) {
            println(Smoke.INFO,(Throwable)object,null);
        } else {
            String message = StringUtil.toString(object);
            println(Smoke.INFO,null,message);
        }
    }

    @Override
    public void info(String message, Object... args) {
        println(Smoke.INFO,null,message,args);
    }

    @Override
    public void warn() {
        println(Smoke.WARN,null,null);
    }

    @Override
    public void warn(Object object) {
        if (object != null && object instanceof Throwable) {
            println(Smoke.WARN,(Throwable)object,null);
        } else {
            String message = StringUtil.toString(object);
            println(Smoke.WARN,null,message);
        }
    }

    @Override
    public void warn(Throwable throwable, String message, Object... args) {
        println(Smoke.WARN,throwable,message,args);
    }

    @Override
    public void warn(String message, Object... args) {
        println(Smoke.WARN,null,message,args);
    }

    @Override
    public void error() {
        println(Smoke.ERROR,null,null);
    }

    @Override
    public void error(Object object) {
        if (object != null && object instanceof Throwable) {
            println(Smoke.ERROR,(Throwable) object,null);
        } else {
            String message = StringUtil.toString(object);
            println(Smoke.ERROR,null,message);
        }
    }

    @Override
    public void error(String message, Object... args) {
        println(Smoke.ERROR,null,message,args);
    }

    @Override
    public void error(Throwable throwable, String message, Object... args) {
        println(Smoke.ERROR,throwable,message,args);
    }

    @Override
    public void log(int level, String tag, String message, Object... args) {
        println(level,tag,null,message,args);
    }

    @Override
    public void xml(int level, String tag, String xml) {
        String message = StringUtil.xml2String(xml);
        println(level,tag,null,message);
    }

    @Override
    public void xml(int level, String xml) {
        String message = StringUtil.xml2String(xml);
        println(level,null,message);
    }

    @Override
    public void json(int level, String tag, String json) {
        String message = StringUtil.json2String(json);
        println(level,tag,null,message);
    }

    @Override
    public void json(int level, String json) {
        String message = StringUtil.json2String(json);
        println(level,null,message);
    }

    public SubSmoke newSub(String sub) {
        SubSmoke newSub = clone();
        newSub.mTags.addLast(sub);
        return newSub;
    }

    public SubSmoke newSub(String sub, Processes processes) {
        SubSmoke newSub = clone();
        if (processes != null) {
            newSub.setProcesses(processes);
        }
        if (!StringUtil.isEmpty(sub)) {
            newSub.mTags.addLast(sub);
        }
        return newSub;
    }

    public void close() {
        if (mProcesses != null) {
            mProcesses.decreaseReference();
            mProcesses.close();

            mProcesses.clear();
        }
    }

    public synchronized void open() {
        if (SMOKE_DISABLE) {
            if (Smoke.DEBUG >= mLogPriority) {
                System.out.println("[Smoke] An exception has occurred and Smoke is turned off!");
            }
            return;
        }

        if (!isOpened ) {
            isOpened = true;
            mProcesses.open();
        }
    }

    public SubSmoke clone() {
        // TODO: 2017/3/15  share the Processes instance?
        SubSmoke newSub = new SubSmoke("", mProcesses);
        newSub.setExtraMethodOffset(0);
        newSub.setLogPriority(mLogPriority);
        newSub.mTags.clear();
        CollectionUtil.addAll(newSub.mTags,mTags);
        return newSub;
    }

    protected void println(int level, Throwable throwable, String message, Object... args) {
        if (level >= mLogPriority && !SMOKE_DISABLE) {
            String firstTAG = mTags.getFirst();
            Smoke.LogBean logBean = createBean(level,firstTAG, mTags,throwable,message,args);
            goChain(logBean);
        } else if (SMOKE_DISABLE && Smoke.DEBUG >= mLogPriority) {
            System.out.println("[Smoke] An exception has occurred and Smoke is turned off!");
        }
    }

    protected void println(int level, String tag,Throwable throwable, String message, Object... args) {
        if (level >= mLogPriority && !SMOKE_DISABLE) {
            Smoke.LogBean logBean = createBean(level,tag, mTags,throwable,message,args);
            goChain(logBean);
        } else if (SMOKE_DISABLE && Smoke.DEBUG >= mLogPriority) {
            System.out.println("[Smoke] An exception has occurred and the function is turned off!");
        }
    }

    private Smoke.LogBean createBean(int level, String tag,List<String> subTags,
                                     Throwable throwable, String message, Object[] args) {
        StackTraceElement traceElement = getTraceElement();
        Smoke.LogBean logBean = new Smoke.LogBean(level,tag,subTags,message,traceElement,throwable,args);
        return logBean;
    }

    protected void goChain(Smoke.LogBean bean) {
        Smoke.Process.Chain chain = new Smoke.Process.Chain(0,mProcesses);
        chain.proceed(bean,new LinkedList<String>());
    }

    protected StackTraceElement getTraceElement() {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        StackTraceElement element = elements[mExtraMethodOffset + 5];
        return element;
    }

    private static class SubChainProcess extends Smoke.Process {

        private SubSmoke SubSmoke;
        public SubChainProcess(SubSmoke SubSmoke) {
            this.SubSmoke = SubSmoke;
        }

        @Override
        public boolean proceed(Smoke.LogBean logBean, List<String> messages, Chain chain) {
            if (SubSmoke != null) {
                SubSmoke.goChain(logBean);
            }
            return false;
        }
    }
}
