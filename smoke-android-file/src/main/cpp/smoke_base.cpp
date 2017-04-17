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
    const char *lines[1];
    lines[0] = message;
    _write_log_array(level,tag,lines,1);
}

void smoke::_write_log_array(const int level, const char *tag, const char **message,int length) {
    smoke::SmokeLog smoke_log(level, tag, message, length);
    __smoke_write_impl(smoke_log);
}

void smoke::_open(int _append_mode,const char *_file_dir, const char *_cache_dir, const char *_name_prefix) {
    appender_open((AppenderMode)_append_mode,_file_dir,_cache_dir,_name_prefix);
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

const char* smoke::_get_log_dir() {
    std::string* current_log_dir = appender_get_current_log_dir();
    return current_log_dir == NULL? NULL : current_log_dir->c_str();
}

const char* smoke::_get_current_file_path() {
    std::string* current_log_path = appender_get_current_file_path();
    return current_log_path == NULL? NULL : current_log_path->c_str();
}


void smoke::_get_logs_from_timespan(const int _timespan, const char* _name_prefix,std::vector<std::string> &vector) {
    appender_get_filepath_from_timespan(_timespan,_name_prefix,vector);
}


