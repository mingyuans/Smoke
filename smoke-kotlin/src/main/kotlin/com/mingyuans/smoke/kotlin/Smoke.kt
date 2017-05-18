package com.mingyuans.smoke.kotlin

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

    fun verbose(tag: String?= null, error: Throwable?= null, message: String) {
        smokeImpl?.verbose(tag = tag, error = error,message=message)
    }

    fun warn(message: String) {
        smokeImpl?.warn(message=message)
    }

    fun debug(message: String) {
        smokeImpl?.debug(message =message);
    }

    fun error(tag: String?= null, error: Throwable?= null, message: String) {

    }


}