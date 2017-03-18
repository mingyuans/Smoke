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

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by yanxq on 2017/3/2.
 */

public class SmokeSub implements ISmoke {
    protected static boolean isDisableVersion = false;

    protected Context mContext;
    protected int mLogPriority = Log.VERBOSE;
    protected int mExtraMethodOffset = 0;
    protected Processes mProcesses;
    protected final LinkedList<String> mTags = new LinkedList<String>();

    public SmokeSub(Context context,String tag) {
        this(context,tag,null);
    }

    public SmokeSub(Context context,String tag,Processes processes) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null!");
        }
        mContext = context.getApplicationContext();

        if (!TextUtils.isEmpty(tag)) {
            mTags.addLast(tag);
        } else if (CollectionUtil.isEmpty(mTags)) {
            mTags.addFirst(mContext.getPackageName());
        }

        if (processes == null) {
            processes = Processes.newDefault(mContext);
        }
        setProcesses(processes);

        isDisableVersion = SmokeUncaughtErrorHandler.isDisableVersion(mContext);
    }

    public void attach(Printer printer) {
        if (printer != null) {
            mProcesses.addLast(new Printer.PrinterProcess(printer));
        }
    }

    public void attach(SmokeSub smokeSub) {
        mProcesses.clear();
        mProcesses.addFirst(new SubChainProcess(smokeSub));
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
        println(Log.VERBOSE,null,null);
    }

    @Override
    public void verbose(Object object) {
        String message = StringUtil.toString(object);
        println(Log.VERBOSE,null,message);
    }

    @Override
    public void verbose(String message, Object... args) {
        println(Log.VERBOSE,null,message,args);
    }

    @Override
    public void debug() {
        println(Log.DEBUG,null,null);
    }

    @Override
    public void debug(Object object) {
        if (object != null && object instanceof Throwable) {
            println(Log.DEBUG,(Throwable)object,null);
        } else {
            String message = StringUtil.toString(object);
            println(Log.DEBUG,null,message);
        }
    }

    @Override
    public void debug(String message, Object... args) {
        println(Log.DEBUG,null,message,args);
    }

    @Override
    public void info() {
        println(Log.INFO,null,null);
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

    public SmokeSub newSub(String sub) {
        SmokeSub newSub = clone();
        newSub.mTags.addLast(sub);
        return newSub;
    }

    public SmokeSub newSub(String sub, Processes processes) {
        SmokeSub newSub = clone();
        if (processes != null) {
            newSub.setProcesses(processes);
        }
        if (!TextUtils.isEmpty(sub)) {
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

    public SmokeSub clone() {
        // TODO: 2017/3/15  share the Processes instance?
        SmokeSub newSub = new SmokeSub(mContext,"", mProcesses);
        newSub.setExtraMethodOffset(0);
        newSub.setLogPriority(mLogPriority);
        newSub.mTags.clear();
        CollectionUtil.addAll(newSub.mTags,mTags);
        return newSub;
    }

    protected void println(int level, Throwable throwable, String message, Object... args) {
        if (level >= mLogPriority && !isDisableVersion) {
            String firstTAG = mTags.getFirst();
            Smoke.LogBean logBean = createBean(level,firstTAG, mTags,throwable,message,args);
            goChain(logBean);
        } else if (isDisableVersion && Smoke.DEBUG >= mLogPriority) {
            String firstTAG = mTags.getFirst();
            Log.d(firstTAG,"An exception has occurred and Smoke is turned off!");
        }
    }

    protected void println(int level, String tag,Throwable throwable, String message, Object... args) {
        if (level >= mLogPriority && !isDisableVersion) {
            Smoke.LogBean logBean = createBean(level,tag, mTags,throwable,message,args);
            goChain(logBean);
        } else if (isDisableVersion && Smoke.DEBUG >= mLogPriority) {
            Log.d(tag,"An exception has occurred and the function is turned off!");
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
        StackTraceElement element = elements[mExtraMethodOffset + 6];
        return element;
    }

    private static class SubChainProcess extends Smoke.Process {

        private SmokeSub smokeSub;
        public SubChainProcess(SmokeSub smokeSub) {
            this.smokeSub = smokeSub;
        }

        @Override
        public boolean proceed(Smoke.LogBean logBean, List<String> messages, Chain chain) {
            if (smokeSub != null) {
                smokeSub.goChain(logBean);
            }
            return false;
        }
    }
}
