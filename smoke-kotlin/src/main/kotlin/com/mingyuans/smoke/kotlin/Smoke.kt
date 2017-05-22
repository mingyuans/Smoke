package com.mingyuans.smoke.kotlin

import com.sun.org.apache.xpath.internal.operations.Bool
import java.util.*

/**
 * Created by yanxq on 17/5/18.
 */
object Smoke {

    val NONE: Int = -1;
    val VERBOSE: Int = 2;
    val DEBUG: Int = 3;
    val INFO: Int = 4;
    val WARN: Int = 5;
    val ERROR: Int = 6;
    val ASSERT: Int = 7;

    private var smokeImpl:SubSmoke? = null


    fun install(tag: String) {
        smokeImpl = SubSmoke(tag)
    }

    fun install(smoke: SubSmoke) {
        smokeImpl = smoke
    }

    fun verbose(tag: String?=null, error: Throwable?=null, message: Any?=null, vararg args: Any?) {
        smokeImpl?.verbose(tag, error, message, *args)
    }

    fun warn(tag: String?=null, error: Throwable?=null, message: Any?=null, vararg args: Any?) {
        smokeImpl?.warn(tag, error, message, *args)
    }

    fun debug(tag: String?=null, error: Throwable?=null, message: Any?=null, vararg args: Any?) {
        smokeImpl?.debug(tag, error, message, *args)
    }

    fun error(tag: String?=null, error: Throwable?=null, message: Any?=null, vararg args: Any?) {
        smokeImpl?.error(tag, error, message, *args)
    }

    fun info(tag: String?=null, error: Throwable?=null, message: Any?=null, vararg args: Any?) {
        smokeImpl?.info(tag, error, message, *args)
    }

    abstract class Process {
        fun close() {}
        fun open() {}
        abstract fun proceed(logBean: LogBean,lines:Array<String>,chain: Chain):Boolean

        class Chain constructor(processes: Array<Process>,index:Int = 0) {
            private var processes:Array<Process> = processes
            private var index:Int = index

            fun proceed(logBean: LogBean,lines: Array<String>): Boolean {
                if (index >= processes.size) {
                    return true
                }

                return processes[index++].proceed(logBean, lines,this)
            }
        }
    }

    class LogBean constructor(tag: String?,priority: Int, error: Throwable?,content: Any?,args: Array<out Any?>){
        var tag: String = ""
        var priority: Int = DEBUG
        var error: Throwable? = null
        var content: Any? = null
        var args: Array<*>? = null
        var stackTrackElement: StackTraceElement? = null
        var extraParams: Map<String,String> = HashMap()
        get() = field
        set(value) {field = value}
    }

}