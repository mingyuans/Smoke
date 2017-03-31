// Tencent is pleased to support the open source community by making Mars available.
// Copyright (C) 2016 THL A29 Limited, a Tencent company. All rights reserved.

// Licensed under the MIT License (the "License"); you may not use this file except in
// compliance with the License. You may obtain a copy of the License at
// http://opensource.org/licenses/MIT

// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific language governing permissions and
// limitations under the License.


/*
 * log_formater.cpp
 *
 *  Created on: 2013-3-8
 *      Author: yerungui
 */


#include <assert.h>
#include <stdio.h>
#include <algorithm>

#include "ptrbuffer.h"
#include "smoke_base.h"
#include "smoke_jni_log.h"

using namespace std;

void log_format(const smoke::SmokeLog *_info, const char* _log_body, PtrBuffer& _log) {
    static const char* levelStrings[] = {
        "U",  //unknown
        "DF", //default
        "V",  //verbose
        "D",  // debug
        "I",  // info
        "W",  // warn
        "E",  // error
        "F"  // fatal
    };

    assert((unsigned int)_log.Pos() == _log.Length());

    static int error_count = 0;
    static int error_size = 0;

    if (_log.MaxLength() <= _log.Length() + 5 * 1024) {  // allowd len(_log) <= 11K(16K - 5K)
        ++error_count;
        error_size = (int)strnlen(_log_body, 1024 * 1024);

        if (_log.MaxLength() >= _log.Length() + 128) {
            int ret = snprintf((char*)_log.PosPtr(), 1024, "[F]log_size <= 5*1024, err(%d, %d)\n", error_count, error_size);  // **CPPLINT SKIP**
            _log.Length(_log.Pos() + ret, _log.Length() + ret);
            _log.Write("");

            error_count = 0;
            error_size = 0;
        }

        smoke_jni::console_debug(__FUNCTION__,"buf.MaxLength <= buf.length + 5 * 1024");
        return;
    }

    if (NULL != _info) {
        char temp_time[64] = {0};

        if (0 != _info->log_timeval.tv_sec) {
            time_t sec = _info->log_timeval.tv_sec;
            tm tm = *localtime((const time_t*)&sec);
#ifdef ANDROID
            snprintf(temp_time, sizeof(temp_time), "%d-%02d-%02d %+.1f %02d:%02d:%02d.%.3ld", 1900 + tm.tm_year, 1 + tm.tm_mon, tm.tm_mday,
                     tm.tm_gmtoff / 3600.0, tm.tm_hour, tm.tm_min, tm.tm_sec, _info->log_timeval.tv_usec / 1000);
#elif _WIN32
            snprintf(temp_time, sizeof(temp_time), "%d-%02d-%02d %+.1f %02d:%02d:%02d.%.3d", 1900 + tm.tm_year, 1 + tm.tm_mon, tm.tm_mday,
                     (-_timezone) / 3600.0, tm.tm_hour, tm.tm_min, tm.tm_sec, _info->log_timeval.tv_usec / 1000);
#else
            snprintf(temp_time, sizeof(temp_time), "%d-%02d-%02d %+.1f %02d:%02d:%02d.%.3d", 1900 + tm.tm_year, 1 + tm.tm_mon, tm.tm_mday,
                     tm.tm_gmtoff / 3600.0, tm.tm_hour, tm.tm_min, tm.tm_sec, _info->log_timeval.tv_usec / 1000);
#endif
        }

        char log_head_buf[128];
        //[[time] [pid-tid] [level/][tag:] [line_array]
        snprintf(log_head_buf, 128, "%s %d %s/%s",  // **CPPLINT SKIP**
                           temp_time, _info->pid, levelStrings[_info->level],_info->tag);

        char *_log_body_temp = strdup(_log_body);
        char *line;
        long length = _log.MaxLength();
        for (line = strsep(&_log_body_temp,"\n"); line != NULL && length > 0; line = strsep(&_log_body_temp,"\n")) {
            if (strlen(line) == 0) {
                continue;
            }
            int ret = snprintf((char*)_log.PosPtr(), length, "%s: %s\n", log_head_buf, line);
            if (ret >= length) {
                smoke_jni::console_debug(__FUNCTION__,"No enough space to fill the log. ret[%d] len[%d]",ret,length);
                break;
            }
            int write_length = ret <= length? ret : length;
            _log.Length(_log.Pos() + write_length, _log.Length() + write_length);
            length -= write_length;
        }
        assert((unsigned int)_log.Pos() == _log.Length());
    }
}

