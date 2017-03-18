//
// Created by yanxq on 2017/2/20.
//

#include <sys/time.h>
extern int get_pid();
extern long get_thread_id();

#ifndef SMOKE_SMOKE_BASE_H
#define SMOKE_SMOKE_BASE_H


typedef enum smoke_priority {
    LOG_VERBOSE = 2,
    LOG_DEBUG,
    LOG_INFO,
    LOG_WARN,
    LOG_ERROR,
    LOG_FATAL,
} priority;

namespace smoke {
    class SmokeLog {
    public:
        int level;
        const char *tag;
        const char **line_array;
        int array_length;
        int pid = 0;
        long tid = 0;
        timeval log_timeval;

        SmokeLog(const int level, const char *tag, const char **message, int length) {
            SmokeLog::level = level;
            SmokeLog::tag = tag;
            SmokeLog::line_array = message;
            SmokeLog::array_length = length;
            gettimeofday(&log_timeval,NULL);
            pid = get_pid();
            tid = get_thread_id();
        }
    };

#ifdef __cplusplus
    extern "C" {
#endif

    void _console_println(int level, const char *tag, const char *message);

    void _write_log(const int level, const char *tag, const char *message);

    void _write_log_array(const int level, const char *tag, const char **message,int length);

    void _open(int _append_mode,const char *_file_dir, const char *_cache_dir, const char *_name_prefix);

    void _flush();

    void _flush_sync();

    void _close();

#ifdef __cplusplus
    }
#endif
}

#endif //SMOKE_SMOKE_BASE_H

