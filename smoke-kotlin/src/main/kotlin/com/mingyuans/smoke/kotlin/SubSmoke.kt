package com.mingyuans.smoke.kotlin

/**
 * Created by yanxq on 17/5/18.
 */
class SubSmoke(tag: String) {
    val globalTag = tag
    private var extraMethodOffset = 0
    get() = field
    set(value) {field = value}

    fun info(tag: String?=null, error: Throwable?=null, message: Any?=null, vararg args: Any?) {
        println(Smoke.INFO, tag?: globalTag, error, message,args)
    }

    fun warn(tag: String?=null, error: Throwable?=null, message: Any?=null, vararg args: Any?) {
        println(Smoke.WARN, tag?: globalTag, error, message,args)
    }

    fun debug(tag: String?=null, error: Throwable?=null, message: Any?=null, vararg args: Any?) {
        println(Smoke.DEBUG, tag?: globalTag, error, message,args)
    }

    fun verbose(tag: String?=null, error: Throwable?=null, message: Any?=null, vararg args: Any?) {
        println(Smoke.VERBOSE, tag?: globalTag, error, message,args)
    }

    fun error(tag: String?=null, error: Throwable?=null, message: Any?=null, vararg args: Any?) {
        println(Smoke.ERROR,tag?: globalTag, error, message,args)
    }

    fun println(priority: Int, tag: String, error: Throwable?, message: Any?, args: Array<*>?) {
        var realArgs = (if (args?.size == 1 && (args?.get(0) is Array<*>)) args?.get(0) else args) as Array<*>
        var full_message = StringUtil.formatString(message,realArgs)
        val priorityString = StringUtil.priority2String(priority)
        var logBean = Smoke.LogBean(tag,priority,error,message,args as Array<out Any?>)
        logBean.stackTrackElement = getTargetMethodElement()
        println("$priorityString/$tag: $full_message \n ${StringUtil.throwable2String(error)}")
    }

    fun getTargetMethodElement(): StackTraceElement {
        var elements = Thread.currentThread().stackTrace
        return elements[3 + extraMethodOffset]
    }

}