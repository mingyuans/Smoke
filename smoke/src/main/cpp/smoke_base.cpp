//
// Created by yanxq on 2017/2/20.
//

#include <cstdio>
#include <android/log.h>
#include "smoke_base.h"
#include "smoke_appender.h"

static const char * __default_tag = "smoke_jni";

void __android_println(int level, const char * tag, const char *message) {
    if (tag == NULL) {
        tag = __default_tag;
    }
    __android_log_print(level,tag,message,NULL);
}

void __smoke_write_impl(smoke::SmokeLog &smokeLog) {
    append_log(smokeLog);
}

void smoke::_console_println(int level, const char *tag, const char *message) {
    __android_println(level,tag,message);
}

void smoke::_write_log(const int level, const char *tag, const char *message) {
    smoke::SmokeLog smoke_log(level, (char *) tag, (char *) message);
    __smoke_write_impl(smoke_log);
}

void smoke::_open(const char *_file_dir, const char *_cache_dir, const char *_name_prefix) {
    appender_open(TAppenderMode::appenderAsync,_file_dir,_cache_dir,_name_prefix);
}

void smoke::_flush() {
    appender_flush();
}

void smoke::_flush_sync() {
    appender_flush_sync();
}

void smoke::_close() {
    appender_close();
}
