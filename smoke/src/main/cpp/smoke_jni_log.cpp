//
// Created by yanxq on 17/2/21.
//

#include <cstdio>
#include "smoke_jni_log.h"
#include "smoke_base.h"

static int __log_priority = smoke_priority::LOG_VERBOSE;

void set_log_priority(int priority) {
    __log_priority = priority;
}

void smoke_jni::console_println(int level, const char *tag, const char *function, const char *fmt, ...) {
    if (level < __log_priority) {
        return;
    }

    va_list arg_list;
    va_start(arg_list,fmt);

    size_t buf_size = snprintf(nullptr,0,fmt,arg_list) + 1;
    char messagep[buf_size];
    snprintf(messagep,buf_size,fmt,arg_list);
    smoke::_console_println(level,tag,messagep);

    va_end(arg_list);
}

void smoke_jni::console_verbose(const char *function, const char *fmt, ...) {
    va_list arg_list;
    va_start(arg_list,fmt);

    smoke_jni::console_println(smoke_priority::LOG_VERBOSE, __global_tag, function, fmt, arg_list);

    va_end(arg_list);
}

void smoke_jni::console_debug(const char *function, const char *fmt, ...) {
    va_list arg_list;
    va_start(arg_list,fmt);

    smoke_jni::console_println(smoke_priority::LOG_VERBOSE, __global_tag, function, fmt, arg_list);

    va_end(arg_list);
}

void smoke_jni::console_info(const char *function, const char *fmt, ...) {
    va_list arg_list;
    va_start(arg_list,fmt);

    smoke_jni::console_println(smoke_priority::LOG_INFO, __global_tag, function, fmt, arg_list);

    va_end(arg_list);
}

void smoke_jni::console_warn(const char *function, const char *fmt, ...) {
    va_list arg_list;
    va_start(arg_list,fmt);

    smoke_jni::console_println(smoke_priority::LOG_WARN, __global_tag, function, fmt, arg_list);

    va_end(arg_list);
}

void smoke_jni::console_error(const char *function, const char *fmt, ...) {
    va_list arg_list;
    va_start(arg_list,fmt);

    smoke_jni::console_println(smoke_priority::LOG_ERROR, __global_tag, function, fmt, arg_list);

    va_end(arg_list);
}