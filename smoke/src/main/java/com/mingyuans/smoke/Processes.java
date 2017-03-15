package com.mingyuans.smoke;

import android.content.Context;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by yanxq on 17/3/15.
 */

public class Processes extends LinkedList<Smoke.Process> {
    private volatile int mPrinterIndex = 0;

    public Processes addPrinter(Smoke.Process process) {
        addLast(process);
        return this;
    }

    public Processes addPrinterFirst(Smoke.Process process) {
        add(mPrinterIndex+1,process);
        return this;
    }

    public Processes addCollector(Smoke.Process process) {
        add(mPrinterIndex++,process);
        return this;
    }

    public Processes addCollectorFirst(Smoke.Process process) {
        addFirst(process);
        mPrinterIndex++;
        return this;
    }

    public int getPrinterIndex() {
        return mPrinterIndex;
    }

    public Processes clone() {
        Processes processes = new Processes();
        CollectionUtil.addAll(processes,this);
        return processes;
    }

    public void close() {
        Iterator<Smoke.Process> iterator = iterator();
        while (iterator.hasNext()) {
            iterator.next().close();
        }
    }

    public void open(Context context) {
        Iterator<Smoke.Process> iterator = iterator();
        while (iterator.hasNext()) {
            iterator.next().open(context);
        }
    }

    public Processes clearProcess() {
        clear();
        return this;
    }

    public static Processes newDefault(Context context) {
        Processes processes = new Processes();
        processes.addCollectorFirst(new LineInitialProcess())
                .addCollector(new DrawBoxProcess())
                .addPrinter(new ConsolePrinter());
        return processes;
    }
}
