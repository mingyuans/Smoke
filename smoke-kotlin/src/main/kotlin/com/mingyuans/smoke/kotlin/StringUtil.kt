package com.mingyuans.smoke.kotlin

import java.io.PrintWriter
import java.io.StringReader
import java.io.StringWriter
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

/**
 * Created by yanxq on 17/5/18.
 */
object StringUtil {

    inline fun Array<*>.smoke2String():String {
        var arrayStringBuilder = StringBuilder()
        forEach { it->
            arrayStringBuilder.append("," + StringUtil.toString(it))
        }
        arrayStringBuilder.append("]")
        return arrayStringBuilder.toString().replaceFirst(",","[")
    }

    fun priority2String(priority: Int): String {
        when (priority) {
            Smoke.DEBUG -> return "D"
            Smoke.INFO -> return "I"
            Smoke.VERBOSE -> return "V"
            Smoke.ERROR -> return "E"
            Smoke.WARN -> return "W"
            Smoke.ASSERT -> return "A"
            else -> {
                return "U"
            }
        }
    }

    fun throwable2String(error: Throwable?): String {
        var lastCause: Throwable? = error
        while (lastCause?.cause != null) {
            lastCause = lastCause?.cause
        }

        val sw = StringWriter()
        val pw = PrintWriter(sw)
        lastCause?.printStackTrace(pw)
        pw.flush()
        return sw.toString()
    }

    fun toString(arg: Any?): String {
        return Format.values()
                .firstOrNull { it.isInstanceOf(arg) }
                ?.toString(arg)
                ?: arg.toString()
    }

    fun formatString(message:Any?, args: Array<*>?): String {
        if (args != null && args.isNotEmpty() && message != null && message is String) {
           var argStrings = Array(args.size,{index-> toString(args[index])})
            return message.format(args=*argStrings)
        } else {
            return toString(message)
        }
    }

    enum class Format {
        Json {
            override fun isInstanceOf(arg: Any?): Boolean {
                return arg is String && arg.trim().get(0)=='{'
            }

            override fun toString(arg: Any?): String {
                return StringUtil.formatJsonString(arg as String?,1)
            }
        },

        Xml {
            override fun isInstanceOf(arg: Any?): Boolean {
                return arg is String && arg.trim().startsWith("<")
            }

            override fun toString(arg: Any?): String {
                return StringUtil.xml2String(arg as String)
            }
        },

        Array {
            override fun isInstanceOf(arg: Any?): Boolean {
                return arg is kotlin.Array<*>
            }

            override fun toString(arg: Any?): String {
                var arrayObject = arg as kotlin.Array<*>
                return arrayObject.smoke2String()
            }
        };

        abstract fun isInstanceOf(arg: Any?): Boolean;
        abstract fun toString(arg: Any?):String;

    }

    fun formatJsonString(json: String?, intentStep: Int): String {
        if (json == null || json.trim().isEmpty()) {
            return ""
        }

        var intentCount = 0
        var current: Char = ' '
        var before: Char = ' '
        val builder = StringBuilder()
        var index = 0
        val length = json.length
        while (index < length) {
            before = current
            current = json[index]
            if (current == '{' || current == '[') {
                builder.append(current)
                builder.append('\n')
                intentCount += intentStep
                appendIntent(builder, intentCount)
            } else if (current == '}' || current == ']') {
                builder.append('\n')
                intentCount -= intentStep
                appendIntent(builder, intentCount)
                builder.append(current)
            } else if (current == ',') {
                builder.append(current)
                if (before != '\\') {
                    builder.append('\n')
                    appendIntent(builder, intentCount)
                }
            } else {
                builder.append(current)
            }
            index++
        }
        return builder.toString()
    }

    private fun appendIntent(builder: StringBuilder, count: Int) {
        for (i in 0..(count - 1)) {
            builder.append("\t")
        }
    }

    fun xml2String(xml: String): String {
        if (!xml.isEmpty()) {
            val xmlInput = StreamSource(StringReader(xml))
            val xmlOutput = StreamResult(StringWriter())
            val transformer = TransformerFactory.newInstance().newTransformer()
            transformer.setOutputProperty(OutputKeys.INDENT, "yes")
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
            transformer.transform(xmlInput, xmlOutput)
            return xmlOutput.writer.toString().replaceFirst(">".toRegex(), ">\n")
        }

        return xml
    }
}