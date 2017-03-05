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

    void console_verbose(const char *function, const char *fmt, ...);

    void console_debug(const char *function, const char *fmt, ...);

    void console_info(const char *function, const char *fmt, ...);

    void console_warn(const char *function, const char *fmt, ...);

    void console_error(const char *function, const char *fmt, ...);
}

#endif //SMOKE_SMOKE_JNI_LOG_H
