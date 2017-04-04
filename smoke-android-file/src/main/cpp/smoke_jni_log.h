//
// Created by yanxq on 17/2/21.
//

#ifndef SMOKE_SMOKE_JNI_LOG_H
#define SMOKE_SMOKE_JNI_LOG_H


#include "smoke_base.h"

static const char *__global_tag = "Smoke_jni";

namespace smoke_jni {
    void set_log_priority(int priority);

    void console_println(int level, const char *tag, const char *function, const char *fmt, ...);

    void va_console_println(int level, const char *tag, const char *function, const char *fmt, va_list args);

    void console_verbose(const char *function, const char *fmt, ...);


    void va_console_verbose(const char *function, const char *fmt, va_list args);

    void console_debug(const char *function, const char *fmt, ...);

    void va_console_debug(const char *function, const char *fmt, va_list args);

    void console_info(const char *function, const char *fmt, ...);

    void va_console_info(const char *function, const char *fmt, va_list args);

    void console_warn(const char *function, const char *fmt, ...);

    void va_console_warn(const char *function, const char *fmt, va_list args);

    void console_error(const char *function, const char *fmt, ...);

    void va_console_error(const char *function, const char *fmt, va_list args);
}

#endif //SMOKE_SMOKE_JNI_LOG_H
