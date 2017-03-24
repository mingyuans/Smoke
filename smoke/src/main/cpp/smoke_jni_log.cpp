//
// Created by yanxq on 17/2/21.
//

#include <cstdio>
#include <string>
#include "smoke_jni_log.h"
#include "smoke_base.h"

using namespace smoke_jni;
static int __log_priority = smoke_priority::LOG_VERBOSE;

void set_log_priority(int priority) {
    __log_priority = priority;
}

static void __va_console_println(int level, const char *tag, const char *function,
                                 const char *fmt, va_list _vars) {
    if (level < __log_priority) {
        return;
    }


    char fmt_buf[4 * 1024] = {0};
    vsnprintf(fmt_buf, sizeof(fmt_buf),fmt,_vars);

    unsigned int full_size = strlen(function) + strlen(fmt_buf) + 4;
    char full_buf[full_size];
    memset(full_buf, 0,sizeof(full_buf));
    strcpy(full_buf,"[");
    strcat(full_buf,function);
    strcat(full_buf,"] ");
    strcat(full_buf,fmt_buf);
    smoke::_console_println(level,tag,full_buf);
}

void smoke_jni::console_println(int level, const char *tag, const char *function, const char *fmt, ...) {
    va_list arg_list;
    va_start(arg_list,fmt);
    __va_console_println(level, tag, function, fmt, arg_list);
    va_end(arg_list);
}

void smoke_jni::va_console_println(int level, const char *tag, const char *function, const char *fmt, va_list arg_list) {
    __va_console_println(level, tag, function, fmt, arg_list);
}

void smoke_jni::console_verbose(const char *function, const char *fmt, ...) {
    va_list arg_list;
    va_start(arg_list,fmt);
    __va_console_println(smoke_priority::LOG_VERBOSE, __global_tag, function, fmt, arg_list);
    va_end(arg_list);
}

void smoke_jni::va_console_verbose(const char *function, const char *fmt,  va_list arg_list) {
    __va_console_println(smoke_priority::LOG_VERBOSE, __global_tag, function, fmt, arg_list);
}

void smoke_jni::console_debug(const char *function, const char *fmt, ...) {
    va_list arg_list;
    va_start(arg_list,fmt);

    __va_console_println(smoke_priority::LOG_DEBUG, __global_tag, function, fmt, arg_list);

    va_end(arg_list);
}

void smoke_jni::va_console_debug(const char *function, const char *fmt, va_list arg_list) {
    __va_console_println(smoke_priority::LOG_DEBUG, __global_tag, function, fmt, arg_list);
}

void smoke_jni::console_info(const char *function, const char *fmt, ...) {
    va_list arg_list;
    va_start(arg_list,fmt);

    __va_console_println(smoke_priority::LOG_INFO, __global_tag, function, fmt, arg_list);

    va_end(arg_list);
}

void smoke_jni::va_console_info(const char *function, const char *fmt, va_list args) {
    __va_console_println(smoke_priority::LOG_INFO, __global_tag, function, fmt, args);
}

void smoke_jni::console_warn(const char *function, const char *fmt, ...) {
    va_list arg_list;
    va_start(arg_list,fmt);
    __va_console_println(smoke_priority::LOG_WARN, __global_tag, function, fmt, arg_list);
    va_end(arg_list);
}

void smoke_jni::va_console_warn(const char *function, const char *fmt, va_list args) {
    __va_console_println(smoke_priority::LOG_WARN, __global_tag, function, fmt, args);
}

void smoke_jni::console_error(const char *function, const char *fmt, ...) {
    va_list arg_list;
    va_start(arg_list,fmt);
    __va_console_println(smoke_priority::LOG_ERROR, __global_tag, function, fmt, arg_list);
    va_end(arg_list);
}

void smoke_jni::va_console_error(const char *function, const char *fmt, va_list args) {
    __va_console_println(smoke_priority::LOG_ERROR, __global_tag, function, fmt, args);
}