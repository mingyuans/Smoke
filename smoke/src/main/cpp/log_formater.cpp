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
#include "sys/types.h"
#include "time.h"

#define __STDC_FORMAT_MACROS
#include <inttypes.h>

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

        assert(false);
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

        // _log.AllocWrite(30*1024, false);
        //[[time] [pid-tid] [level/][tag:] [message]
        int ret = snprintf((char*)_log.PosPtr(), 1024, "%s %d-%lu %s/%s: %s",  // **CPPLINT SKIP**
                           temp_time, _info->pid,_info->tid, levelStrings[_info->level],_info->tag, _info->message);

        _log.Length(_log.Pos() + ret, _log.Length() + ret);
        //      memcpy((char*)_log.PosPtr() + 1, "\0", 1);

        assert((unsigned int)_log.Pos() == _log.Length());
    }
}

