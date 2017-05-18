package com.mingyuans.smoke.kotlin

import org.junit.Test

/**
 * Created by yanxq on 2017/5/21.
 */
class SubSmokeTest {
    @Test
    fun testDebug() {
        var subSmoke = SubSmoke("Smoke")
        subSmoke.debug(message = "Hello,Kotlin")

        subSmoke.debug(message = arrayOf("Hello,","Kotlin"))
        subSmoke.debug(message = "Hello,%s",args = arrayOf("Kotlin"))

        subSmoke.debug(message = "Hello,array: %s",args = arrayOf(arrayOf("Kotlin","array")))
    }
}