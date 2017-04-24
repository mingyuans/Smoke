//
// Created by yanxq on 2017/2/18.
//

#include <stdarg.h>
#include <cstdio>
#include "smoke_log.h"
#include "smoke_base.h"

static const char *__global_tag = "smoke_jni";
static int __log_priority = smoke_priority::LOG_VERBOSE;
static bool  __console_enable = false;

void smoke_set_global_tag(const char *_global_tag) {
    __global_tag = _global_tag;
}

void smoke_set_log_priority(int priority) {
    __log_priority = priority;
}

void smoke_set_console_enable(bool enable) {
    __console_enable = enable;
}

void smoke_console_println(int level, const char *tag, const char *function, const char *fmt, ...) {
    if (level < __log_priority) {
        return;
    }

    va_list arg_list;
    va_start(arg_list,fmt);

    size_t buf_size = snprintf(nullptr,0,fmt,arg_list) + 1;
    char message[buf_size];
    snprintf(message,buf_size,fmt,arg_list);
    smoke::_console_println(level,tag,message);

    va_end(arg_list);
}

void smoke_write(int level, const char *tag, const char *function, const char *fmt, ...) {
    if (level < __log_priority) {
        return;
    }

    va_list arg_list;
    va_start(arg_list,fmt);

    size_t buf_size = snprintf(nullptr,0,fmt,arg_list) + 1;
    char message[buf_size];
    snprintf(message,buf_size,fmt,arg_list);
    smoke::_write_log(level,tag,message);

    va_end(arg_list);
}

void __smoke_println(int level, const char *tag, const char *function, const char *fmt,...) {
    va_list arg_list;
    va_start(arg_list,fmt);

    if (__console_enable) {
        smoke_console_println(level, tag, function, fmt, arg_list);
    }
    smoke_write(smoke_priority::LOG_VERBOSE,__global_tag,function,fmt,arg_list);

    va_end(arg_list);
}

void smoke_verbose(const char *function, const char *fmt, ...) {
    va_list arg_list;
    va_start(arg_list,fmt);
    __smoke_println(smoke_priority::LOG_VERBOSE,__global_tag,function,fmt,arg_list);
    va_end(arg_list);
}


void smoke_debug(const char *function, const char *fmt, ...) {
    va_list arg_list;
    va_start(arg_list,fmt);
    __smoke_println(smoke_priority::LOG_DEBUG,__global_tag,function,fmt,arg_list);
    va_end(arg_list);
}


void smoke_info(const char *function, const char *fmt, ...) {
    va_list arg_list;
    va_start(arg_list,fmt);
    __smoke_println(smoke_priority::LOG_INFO,__global_tag,function,fmt,arg_list);
    va_end(arg_list);
}


void smoke_warn(const char *function, const char *fmt, ...) {
    va_list arg_list;
    va_start(arg_list,fmt);
    __smoke_println(smoke_priority::LOG_WARN,__global_tag,function,fmt,arg_list);
    va_end(arg_list);
}


void smoke_error(const char *function, const char *fmt, ...) {
    va_list arg_list;
    va_start(arg_list,fmt);
    __smoke_println(smoke_priority::LOG_ERROR,__global_tag,function,fmt,arg_list);
    va_end(arg_list);
}
