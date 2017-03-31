//
// Created by yanxq on 2017/2/18.
//

#ifndef SMOKE_SMOKE_JNI_LOG_H
#define SMOKE_SMOKE_JNI_LOG_H

#include "smoke_base.h"

extern "C" {

void smoke_set_log_priority(int priority);

void smoke_set_global_tag(const char *_global_tag);

void smoke_set_console_enable(bool enable);

void smoke_console_println(int level, const char *tag, const char *function, const char *fmt, ...);

void smoke_write(int level, const char *tag, const char *function, const char *fmt, ...);

void smoke_verbose(const char *function, const char *fmt, ...);

void smoke_debug(const char *function, const char *fmt, ...);

void smoke_info(const char *function, const char *fmt, ...);

void smoke_warn(const char *function, const char *fmt, ...);

void smoke_error(const char *function, const char *fmt, ...);

};

#endif //SMOKE_SMOKE_JNI_LOG_H

