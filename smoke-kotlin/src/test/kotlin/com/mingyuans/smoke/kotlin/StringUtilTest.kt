package com.mingyuans.smoke.kotlin

import org.junit.Test

/**
 * Created by yanxq on 2017/5/21.
 */
class StringUtilTest {
    @Test
    fun formatString() {
        var intArrays = intArrayOf(1,2,3,4,5)
        println(StringUtil.formatString(intArrays,null))
        var testArray = arrayOf("Hello","Kotlin","Array")
        println(StringUtil.formatString(testArray,null))
        var testList = listOf("Hello","Kotlin","List")
        println(StringUtil.formatString(testList,null))

        val testJson = "{name=\"Kotlin\",message=\"Hello\"}"
        println(StringUtil.formatString(testJson,null))


        val testXml = "<team><member name=\"Elvis\"/><member name=\"Leon\"/></team>"
        println(StringUtil.formatString(testXml,null))

        var testNull:String? = null
        println(StringUtil.formatString(testNull,null))

    }

}