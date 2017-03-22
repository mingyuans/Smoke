package com.mingyuans.smoke;

import android.content.Context;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by yanxq on 17/3/15.
 */

public class Processes implements Iterable<Processes.ProcessEntry> {
    private final AtomicInteger mReferenceCount = new AtomicInteger(0);
    private int mPrinterHeadIndex = 0;
    private final LinkedList<String> mIdentifyList = new LinkedList<>();
    private final LinkedHashMap<String,Smoke.Process> mProcessMap = new LinkedHashMap<>();

    private String getIdentify(Object object) {
        return object == null? "null" : getIdentify(object.getClass());
    }

    public static String getIdentify(Class clazz) {
        return clazz.getSimpleName();
    }

    public Processes addPrinter(Smoke.Process process) {
        return addPrinter(getIdentify(process),process);
    }

    public Processes addPrinter(String identify, Smoke.Process process) {
        return addLast(identify,process);
    }

    public Processes addFirst(Smoke.Process process) {
        return addFirst(getIdentify(process),process);
    }

    public Processes addFirst(String identify, Smoke.Process process) {
        synchronized (mIdentifyList) {
            mIdentifyList.addFirst(identify);
            mProcessMap.put(identify,process);
        }
        return this;
    }

    public Processes addLast(Smoke.Process process) {
        return addLast(getIdentify(process),process);
    }

    public Processes addLast(String identify, Smoke.Process process) {
        synchronized (mIdentifyList) {
            mIdentifyList.addLast(identify);
            mProcessMap.put(identify,process);
        }
        return this;
    }

    public Processes add(Smoke.Process process) {
        return add(getIdentify(process),process);
    }

    public Processes add(String identify, Smoke.Process process) {
        return addLast(identify,process);
    }

    public Processes add(int index, Smoke.Process process) {
        return add(index, getIdentify(process),process);
    }

    public Processes add(int index, String identify, Smoke.Process process) {
        synchronized (mIdentifyList) {
            mIdentifyList.add(index,identify);
            mProcessMap.put(identify,process);
        }
        return this;
    }

    public Processes addPrinterFirst(Smoke.Process process) {
        return addPrinterFirst(getIdentify(process),process);
    }

    public Processes addPrinterFirst(String identify, Smoke.Process process) {
        return add(mPrinterHeadIndex++,identify,process);
    }

    public Processes addCollector(Smoke.Process process) {
        return addCollector(getIdentify(process),process);
    }

    public Processes addCollector(String identify,Smoke.Process process) {
        add(mPrinterHeadIndex++,identify,process);
        return this;
    }

    public Processes addCollectorFirst(Smoke.Process process) {
        return addCollectorFirst(getIdentify(process),process);
    }

    public Processes addCollectorFirst(String identify, Smoke.Process process) {
        addFirst(identify,process);
        mPrinterHeadIndex++;
        return this;
    }

    public int getPrinterIndex() {
        return mPrinterHeadIndex;
    }

    public Smoke.Process get(int index) {
        Smoke.Process process = null;
        if (index < mIdentifyList.size()) {
            String identify = mIdentifyList.get(index);
            process = mProcessMap.get(identify);
        }
        return process;
    }

    public Smoke.Process getByIdentify(String identify) {
        return mProcessMap.get(identify);
    }

    public Processes removeByIdentify(String identify) {
        synchronized (mIdentifyList) {
            mIdentifyList.remove(identify);
            mProcessMap.remove(identify);
        }
        return this;
    }

    public Processes remove(int index) {
        synchronized (mIdentifyList) {
            String identify = mIdentifyList.remove(index);
            mProcessMap.remove(identify);
        }
        return this;
    }

    public Processes insertBefore(String target,String identify, Smoke.Process process) {
        synchronized (mIdentifyList) {
            int index = mIdentifyList.indexOf(target);
            if (index != -1) {
                add(index,identify,process);
            } else {
                //I don't know how to do .
            }
        }
        return this;
    }

    public Processes insertBefore(String taraget, Smoke.Process process) {
        return insertBefore(taraget, getIdentify(process),process);
    }

    public Processes insertAfter(String target,String identify, Smoke.Process process) {
        synchronized (mIdentifyList) {
            int index = mIdentifyList.indexOf(target);
            if (index != -1) {
                add(index+1,identify,process);
            } else {
                //I don't know how to do .
            }
        }
        return this;
    }

    public Processes insertAfter(String target, Smoke.Process process) {
        return insertAfter(target,getIdentify(process),process);
    }

    public Processes clone() {
        Processes processes = new Processes();
        CollectionUtil.addAll(processes.mIdentifyList,mIdentifyList);
        Iterator<Map.Entry<String,Smoke.Process>> iterator = mProcessMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String,Smoke.Process> entry = iterator.next();
            processes.mProcessMap.put(entry.getKey(),entry.getValue());
        }
        return processes;
    }

    public Processes clear() {
        synchronized (mIdentifyList) {
            mIdentifyList.clear();
            mProcessMap.clear();
        }
        return this;
    }

    public void close() {
        if (mReferenceCount.get() <= 0) {
            Iterator<ProcessEntry> iterator = iterator();
            while (iterator.hasNext()) {
                ProcessEntry entry = iterator.next();
                if (entry.process != null) {
                    entry.process.close();
                }
            }
        }
    }

    public int size() {
        return mIdentifyList.size();
    }

    public void increaseReference() {
        mReferenceCount.incrementAndGet();
    }

    public void decreaseReference() {
        mReferenceCount.decrementAndGet();
    }

    public void open(Context context) {
        Iterator<ProcessEntry> iterator = iterator();
        while (iterator.hasNext()) {
            ProcessEntry entry = iterator.next();
            if (entry.process != null) {
                entry.process.open(context);
            }
        }
    }

    public static Processes newDefault(Context context) {
        Processes processes = new Processes();
        processes.addCollectorFirst(new LineInitialProcess())
                .addCollector(new DrawBoxProcess())
                .addPrinter(new ConsolePrinter());
        return processes;
    }

    @Override
    public Iterator<ProcessEntry> iterator() {
        return new ProcessIterator();
    }

    public static class ProcessEntry {
        public String identify = "";
        public Smoke.Process process;

        public ProcessEntry(String identify, Smoke.Process process) {
            this.identify = identify;
            this.process = process;
        }
    }

    public class ProcessIterator implements Iterator<ProcessEntry> {
        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < mIdentifyList.size();
        }

        @Override
        public ProcessEntry next() {
            String identify = mIdentifyList.get(index++);
            Smoke.Process process = mProcessMap.get(identify);
            return new ProcessEntry(identify,process);
        }
    }
}
