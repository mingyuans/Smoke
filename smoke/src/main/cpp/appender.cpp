//// Tencent is pleased to support the open source community by making Mars available.
//// Copyright (C) 2016 THL A29 Limited, a Tencent company. All rights reserved.
//
//// Licensed under the MIT License (the "License"); you may not use this file except in
//// compliance with the License. You may obtain a copy of the License at
//// http://opensource.org/licenses/MIT
//
//// Unless required by applicable law or agreed to in writing, software distributed under the License is
//// distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
//// either express or implied. See the License for the specific language governing permissions and
//// limitations under the License.
//
//
///*
// * appender.h
// *
// *  Created on: 2013-3-7
// *      Author: yerungui
// */
//
//#include "appender.h"
//#include <stdio.h>
//#include "smoke_utils/file_util.h"
//#include <mutex>
//#include <thread>
//#include "smoke_utils/strutil.h"
//#include "smoke_jni_log.h"
//#include "log_buffer.h"
//#include "autobuffer.h"
//#include "ptrbuffer.h"
//#include "smoke_utils/mmap_util.h"
//#include "tickcount.h"
//#include "bootrun.h"
//#include "thread/thread.h"
//#include "smoke_utils/time_utils.h"
//#include "thread/condition.h"
//#include "thread/tss.h"
//
//#define __STDC_FORMAT_MACROS
//#include <inttypes.h>
//#include <sys/mount.h>
//
//#include <ctype.h>
//#include <assert.h>
//
//#include <unistd.h>
//#include <zlib.h>
//
//#include <string>
//#include <algorithm>
//#include <dirent.h>
//
//#define LOG_EXT "xlog"
//
//
//static TAppenderMode sg_mode = appenderAsync;
//
//static std::string sg_logdir;
//static std::string sg_cache_logdir;
//static std::string sg_logfileprefix;
//
//static std::recursive_mutex sg_mutex_log_file;
//static FILE* sg_logfile = NULL;
//static time_t sg_openfiletime = 0;
//static std::string sg_current_dir;
//
//static std::recursive_mutex sg_mutex_buffer_async;
//#ifdef _WIN32
//static Condition& sg_cond_buffer_async = *(new Condition());  // 改成引用, 避免在全局释放时执行析构导致crash
//#else
////static Condition sg_cond_buffer_async;
//#endif
//
//static LogBuffer* sg_log_buff = NULL;
//
//static volatile bool __sg_log_close = true;
//
//static Tss sg_tss_dumpfile(&free);
//
//#ifdef DEBUG
//static bool sg_consolelog_open = true;
//#else
//static bool sg_consolelog_open = false;
//#endif
//
//static void __async_log_thread();
////static Thread sg_thread_async(&__async_log_thread);
//
//static const unsigned int kBufferBlockLength = 150 * 1024;
//static const long kMaxLogAliveTime = 10 * 24 * 60 * 60;	// 10 days in second
//
//static std::string sg_log_extra_msg;
//
////static boost::iostreams::mapped_file sg_mmmap_file;
//
//namespace {
//class ScopeErrno {
//  public:
//    ScopeErrno() {m_errno = errno;}
//    ~ScopeErrno() {errno = m_errno;}
//
//  private:
//    ScopeErrno(const ScopeErrno&);
//    const ScopeErrno& operator=(const ScopeErrno&);
//
//  private:
//    int m_errno;
//};
//
//#define SCOPE_ERRNO() SCOPE_ERRNO_I(__LINE__)
//#define SCOPE_ERRNO_I(line) SCOPE_ERRNO_II(line)
//#define SCOPE_ERRNO_II(line) ScopeErrno __scope_errno_##line
//
//}
//
//static void __make_log_file_name(const timeval &_tv, const std::string &_logdir,
//                                 const char *_prefix, const std::string &_fileext, char *_filepath,
//                                 unsigned int _len) {
//    time_t sec = _tv.tv_sec;
//    tm tcur = *localtime((const time_t*)&sec);
//
//    std::string log_file_path = _logdir;
//    log_file_path += "/";
//    log_file_path += _prefix;
//    char temp [64] = {0};
//    snprintf(temp, 64, "_%d%02d%02d", 1900 + tcur.tm_year, 1 + tcur.tm_mon, tcur.tm_mday);
//    log_file_path += temp;
//    log_file_path += ".";
//    log_file_path += _fileext;
//    strncpy(_filepath, log_file_path.c_str(), _len - 1);
//    _filepath[_len - 1] = '\0';
//}
//
//static void __del_files(const std::string& _forder_path) {
//    fileUtil::delete_dir(_forder_path.c_str());
//}
//
//static void __del_timeout_file(const std::string& _log_path) {
//    time_t now_time = time(NULL);
//
//    if (fileUtil::is_dir(_log_path.c_str())) {
//        DIR *dir = opendir(_log_path.c_str());
//        if (dir == NULL) {
//            return;
//        }
//
//        struct dirent *ptr = NULL;
//        char child_path[256];
//        const char *cur_dir = _log_path.c_str();
//
//        while ((ptr = readdir(dir)) != NULL) {
//            if (strcmp(ptr->d_name, ".") == 0) {
//                continue;
//            }
//
//            if (strcmp(ptr->d_name, "..") == 0) {
//                continue;
//            }
//
//            snprintf(child_path,256,"%s/%s",cur_dir,ptr->d_name);
//            long file_last_write_time = fileUtil::last_write_time(child_path);
//
//            if (now_time > file_last_write_time && (now_time - file_last_write_time) > kMaxLogAliveTime) {
//                if (fileUtil::is_file(child_path)) {
//                    remove(child_path);
//                } else {
//                    __del_files(child_path);
//                }
//            }
//        }
//    }
//}
//
//static bool __append_file(const std::string& _src_file, const std::string& _dst_file) {
//    if (_src_file == _dst_file) {
//        return false;
//    }
//
//    if (!fileUtil::exists(_src_file.c_str())){
//        return false;
//    }
//
//    if (0 == fileUtil::file_size(_src_file.c_str())){
//        return true;
//    }
//
//    FILE* src_file = fopen(_src_file.c_str(), "rb");
//
//    if (NULL == src_file) {
//        return false;
//    }
//
//    FILE* dest_file = fopen(_dst_file.c_str(), "ab");
//
//    if (NULL == dest_file) {
//        fclose(src_file);
//        return false;
//    }
//
//    fseek(src_file, 0, SEEK_END);
//    long src_file_len = ftell(src_file);
//    long dst_file_len = ftell(dest_file);
//    fseek(src_file, 0, SEEK_SET);
//
//    char buffer[4096] = {0};
//
//    while (true) {
//        if (feof(src_file)) break;
//
//        size_t read_ret = fread(buffer, 1, sizeof(buffer), src_file);
//
//        if (read_ret == 0)   break;
//
//        if (ferror(src_file)) break;
//
//        fwrite(buffer, 1, read_ret, dest_file);
//
//        if (ferror(dest_file))  break;
//    }
//
//    if (dst_file_len + src_file_len > ftell(dest_file)) {
//        ftruncate(fileno(dest_file), dst_file_len);
//        fclose(src_file);
//        fclose(dest_file);
//        return false;
//    }
//
//    fclose(src_file);
//    fclose(dest_file);
//
//    return true;
//}
//
//static void __move_old_files(const std::string& _src_path, const std::string& _dest_path, const std::string& _nameprefix) {
//    if (_src_path == _dest_path) {
//        return;
//    }
//
//    if (!fileUtil::is_dir(_src_path.c_str())) {
//        return;
//    }
//
//    std::lock_guard<std::recursive_mutex> lock_guard(sg_mutex_log_file);
//
//    std::string ** src_child_files = new std::string *[20];
//    int child_count = fileUtil::find_dir_child_files(_src_path.c_str(),20,src_child_files);
//    for (int i = 0; i < child_count; ++i) {
//        std::string *child_file = src_child_files[i];
//        std::string child_file_full = _src_path + "/" + *child_file;
//        if (!strutil::StartsWith(child_file_full, _nameprefix) || !strutil::EndsWith(*child_file, LOG_EXT)) {
//            continue;
//        }
//
//        std::string des_file_name = _dest_path + "/" + *child_file;
//
//        if (!__append_file(child_file_full, des_file_name)) {
//            break;
//        }
//        fileUtil::remove_file(child_file_full.c_str());
//    }
//}
//
//static void __write_tips2console(const char *_tips_format, ...) {
//    va_list ap;
//    va_start(ap, _tips_format);
//    smoke::_console_println(smoke_priority ::LOG_DEBUG, _tips_format, ap);
//    va_end(ap);
//}
//
//static bool __write_file(const void *_data, size_t _len, FILE *_file) {
//    if (NULL == _file) {
//        assert(false);
//        return false;
//    }
//
//    long before_len = ftell(_file);
//    if (before_len < 0) return false;
//
//    if (1 != fwrite(_data, _len, 1, _file)) {
//        int err = ferror(_file);
//
//        __write_tips2console("_write_log file error:%d", err);
//
//        ftruncate(fileno(_file), before_len);
//        fseek(_file, 0, SEEK_END);
//
//        char err_log[256] = {0};
//        snprintf(err_log, sizeof(err_log), "\nwrite_log file error:%d\n", err);
//
//        char tmp[256] = {0};
//        size_t len = sizeof(tmp);
//        LogBuffer::Write(err_log, strnlen(err_log, sizeof(err_log)), tmp, len);
//
//        fwrite(tmp, len, 1, _file);
//
//        return false;
//    }
//
//    return true;
//}
//
//static bool __open_log_file(const std::string &_log_dir) {
//    if (sg_logdir.empty()) return false;
//
//    struct timeval tv;
//    gettimeofday(&tv, NULL);
//
//    if (NULL != sg_logfile) {
//        time_t sec = tv.tv_sec;
//        tm tcur = *localtime((const time_t*)&sec);
//        tm filetm = *localtime(&sg_openfiletime);
//
//        if (filetm.tm_year == tcur.tm_year && filetm.tm_mon == tcur.tm_mon && filetm.tm_mday == tcur.tm_mday && sg_current_dir == _log_dir) return true;
//
//        fclose(sg_logfile);
//        sg_logfile = NULL;
//    }
//
//    static time_t s_last_time = 0;
//    static uint64_t s_last_tick = 0;
//    static char s_last_file_path[1024] = {0};
//
//    uint64_t now_tick = get_alarm_tick();
//    time_t now_time = tv.tv_sec;
//
//    sg_openfiletime = tv.tv_sec;
//    sg_current_dir = _log_dir;
//
//    char logfilepath[1024] = {0};
//    __make_log_file_name(tv, _log_dir, sg_logfileprefix.c_str(), LOG_EXT, logfilepath, 1024);
//
//    if (now_time < s_last_time) {
//        sg_logfile = fopen(s_last_file_path, "ab");
//
//		if (NULL == sg_logfile) {
//            __write_tips2console("open file error:%d %s, path:%s", errno, strerror(errno),
//                                 s_last_file_path);
//        }
//
//        return NULL != sg_logfile;
//    }
//
//    sg_logfile = fopen(logfilepath, "ab");
//
//	if (NULL == sg_logfile) {
//        __write_tips2console("open file error:%d %s, path:%s", errno, strerror(errno), logfilepath);
//    }
//
//
//    if (0 != s_last_time && (now_time - s_last_time) > (time_t)((now_tick - s_last_tick) / 1000 + 300)) {
//
//        struct tm tm_tmp = *localtime((const time_t*)&s_last_time);
//        char last_time_str[64] = {0};
//        strftime(last_time_str, sizeof(last_time_str), "%Y-%m-%d %z %H:%M:%S", &tm_tmp);
//
//        tm_tmp = *localtime((const time_t*)&now_time);
//        char now_time_str[64] = {0};
//        strftime(now_time_str, sizeof(now_time_str), "%Y-%m-%d %z %H:%M:%S", &tm_tmp);
//
//        char log[1024] = {0};
//        snprintf(log, sizeof(log), "[F][ last log file:%s from %s to %s, time_diff:%ld, tick_diff:%" PRIu64 "\n", s_last_file_path, last_time_str, now_time_str, now_time-s_last_time, now_tick-s_last_tick);
//        char tmp[2 * 1024] = {0};
//        size_t len = sizeof(tmp);
//        LogBuffer::Write(log, strnlen(log, sizeof(log)), tmp, len);
//        __write_file(tmp, len, sg_logfile);
//    }
//
//    memcpy(s_last_file_path, logfilepath, sizeof(s_last_file_path));
//    s_last_tick = now_tick;
//    s_last_time = now_time;
//
//    return NULL != sg_logfile;
//}
//
//static void __close_log_file() {
//    if (NULL == sg_logfile) return;
//
//    sg_openfiletime = 0;
//    fclose(sg_logfile);
//    sg_logfile = NULL;
//}
//
//static void __log2file(const void* _data, size_t _len) {
//	if (NULL == _data || 0 == _len || sg_logdir.empty()) {
//		return;
//	}
//
//    std::lock_guard lock_guard(sg_mutex_log_file);
//
//	if (sg_cache_logdir.empty()) {
//        if (__open_log_file(sg_logdir)) {
//            __write_file(_data, _len, sg_logfile);
//            if (appenderAsync == sg_mode) {
//                __close_log_file();
//            }
//        }
//        return;
//	}
//
//    struct timeval tv;
//    gettimeofday(&tv, NULL);
//    char logcachefilepath[1024] = {0};
//
//    __make_log_file_name(tv, sg_cache_logdir, sg_logfileprefix.c_str(), LOG_EXT, logcachefilepath,
//                         1024);
//
//    if (fileUtil::exists(logcachefilepath) && __open_log_file(sg_cache_logdir)) {
//        __write_file(_data, _len, sg_logfile);
//        if (appenderAsync == sg_mode) {
//            __close_log_file();
//        }
//
//
//        char logfilepath[1024] = {0};
//        __make_log_file_name(tv, sg_logdir, sg_logfileprefix.c_str(), LOG_EXT, logfilepath, 1024);
//        if (__append_file(logcachefilepath, logfilepath)) {
//            if (appenderSync == sg_mode) {
//                __close_log_file();
//            }
//            remove(logcachefilepath);
//        }
//    } else {
//        bool write_sucess = false;
//        bool open_success = __open_log_file(sg_logdir);
//        if (open_success) {
//            write_sucess = __write_file(_data, _len, sg_logfile);
//            if (appenderAsync == sg_mode) {
//                __close_log_file();
//            }
//        }
//
//        if (!write_sucess) {
//            if (open_success && appenderSync == sg_mode) {
//                __close_log_file();
//            }
//
//            if (__open_log_file(sg_cache_logdir)) {
//                __write_file(_data, _len, sg_logfile);
//                if (appenderAsync == sg_mode) {
//                    __close_log_file();
//                }
//            }
//        }
//    }
//
//}
//
//
//static void __write_tips_to_file(const char *_tips_format, ...) {
//
//    if (NULL == _tips_format) {
//        return;
//    }
//
//    char tips_info[4096] = {0};
//    va_list ap;
//    va_start(ap, _tips_format);
//    vsnprintf(tips_info, sizeof(tips_info), _tips_format, ap);
//    va_end(ap);
//
//    char tmp[8 * 1024] = {0};
//    size_t len = sizeof(tmp);
//
//    LogBuffer::Write(tips_info, strnlen(tips_info, sizeof(tips_info)), tmp, len);
//
//    __log2file(tmp, len);
//}
//
//static void __async_log_thread() {
//    while (true) {
//
//        std::lock_guard<std::recursive_mutex> lock_guard(sg_mutex_buffer_async);
//
//        if (NULL == sg_log_buff) break;
//
//        AutoBuffer tmp;
//        sg_log_buff->Flush(tmp);
//        sg_mutex_buffer_async.unlock();
//
//		if (NULL != tmp.Ptr())  __log2file(tmp.Ptr(), tmp.Length());
//
//        if (__sg_log_close) break;
//
////        sg_cond_buffer_async.wait(15 * 60 *1000);
//    }
//}
//
////static void __appender_sync(const smoke::SmokeLog* _info, const char* _log) {
////
////    char temp[16 * 1024] = {0};     // tell perry,ray if you want modify size.
////    PtrBuffer log(temp, 0, sizeof(temp));
////    //todo yanxq 这里需要修改
////    log_formater(_info, _log, log);
////
////    char buffer_crypt[16 * 1024] = {0};
////    size_t len = 16 * 1024;
////    if (!LogBuffer::Write(log.Ptr(), log.Length(), buffer_crypt, len))   return;
////
////    __log2file(buffer_crypt, len);
////}
//
////static void __appender_async(const XLoggerInfo* _info, const char* _log) {
////    std::lock_guard<std::recursive_mutex> buffer_async_lock(sg_mutex_buffer_async);
////    if (NULL == sg_log_buff) return;
////
////    char temp[16*1024] = {0};       //tell perry,ray if you want modify size.
////    PtrBuffer log_buff(temp, 0, sizeof(temp));
////    //todo yanxq 需要修改
////    log_formater(_info, _log, log_buff);
////
////    if (sg_log_buff->GetData().Length() >= kBufferBlockLength*4/5) {
////       int ret = snprintf(temp, sizeof(temp), "[F][ sg_buffer_async.Length() >= BUFFER_BLOCK_LENTH*4/5, len: %d\n", (int)sg_log_buff->GetData().Length());
////       log_buff.Length(ret, ret);
////    }
////
////    if (!sg_log_buff->Write(log_buff.Ptr(), (unsigned int)log_buff.Length())) return;
////
////    if (sg_log_buff->GetData().Length() >= kBufferBlockLength*1/3 || (NULL!=_info && kLevelFatal == _info->level)) {
////       sg_cond_buffer_async.notifyAll();
////    }
////
////}
//
//////////////////////////////////////////////////////////////////////////////////////
//
////void xlogger_appender(const XLoggerInfo* _info, const char* _log) {
////    if (__sg_log_close) return;
////
////    SCOPE_ERRNO();
////
////    DEFINE_SCOPERECURSIONLIMIT(recursion);
////    static Tss s_recursion_str(free);
////
////    if (sg_consolelog_open) ConsoleLog(_info,  _log);
////
////    if (2 <= (int)recursion.Get() && NULL == s_recursion_str.get()) {
////        if ((int)recursion.Get() > 10) return;
////        char* strrecursion = (char*)calloc(16 * 1024, 1);
////        s_recursion_str.set((void*)(strrecursion));
////
////        XLoggerInfo info = *_info;
////        info.level = kLevelFatal;
////
////        char recursive_log[256] = {0};
////        snprintf(recursive_log, sizeof(recursive_log), "ERROR!!! xlogger_appender Recursive calls!!!, count:%d", (int)recursion.Get());
////
////        PtrBuffer tmp(strrecursion, 0, 16*1024);
////        log_formater(&info, recursive_log, tmp);
////
////        strncat(strrecursion, _log, 4096);
////        strrecursion[4095] = '\0';
////
////        ConsoleLog(&info,  strrecursion);
////    } else {
////        if (NULL != s_recursion_str.get()) {
////            char* strrecursion = (char*)s_recursion_str.get();
////            s_recursion_str.set(NULL);
////
////            __write_tips_to_file(strrecursion);
////            free(strrecursion);
////        }
////
////        if (appenderSync == sg_mode)
////            __appender_sync(_info, _log);
////        else
////            __appender_async(_info, _log);
////    }
////}
//
//#define HEX_STRING  "0123456789abcdef"
//static unsigned int to_string(const void* signature, int len, char* str) {
//    char* str_p = str;
//    const unsigned char* sig_p;
//
//    for (sig_p = (const unsigned char*) signature;  sig_p - (const unsigned char*)signature < len; sig_p++) {
//        char high, low;
//        high = *sig_p / 16;
//        low = *sig_p % 16;
//
//        *str_p++ = HEX_STRING[(unsigned char)high];
//        *str_p++ = HEX_STRING[(unsigned char)low];
//        *str_p++ = ' ';
//    }
//
//    *str_p++ = '\n';
//
//    for (sig_p = (const unsigned char*) signature;  sig_p - (const unsigned char*)signature < len; sig_p++) {
//        *str_p++ = char(isgraph(*sig_p) ? *sig_p : ' ');
//        *str_p++ = ' ';
//        *str_p++ = ' ';
//    }
//
//    return (unsigned int)(str_p - str);
//}
//
//const char* xlogger_dump(const void* _dumpbuffer, size_t _len) {
//    if (NULL == _dumpbuffer || 0 == _len) {
//        //        ASSERT(NULL!=_dumpbuffer);
//        //        ASSERT(0!=_len);
//        return "";
//    }
//
//    SCOPE_ERRNO();
//
//    if (NULL == sg_tss_dumpfile.get()) {
//        sg_tss_dumpfile.set(calloc(4096, 1));
//    } else {
//        memset(sg_tss_dumpfile.get(), 0, 4096);
//    }
//
//    struct timeval tv = {0};
//    gettimeofday(&tv, NULL);
//    time_t sec = tv.tv_sec;
//    tm tcur = *localtime((const time_t*)&sec);
//
//    char forder_name [128] = {0};
//    snprintf(forder_name, sizeof(forder_name), "%d%02d%02d", 1900 + tcur.tm_year, 1 + tcur.tm_mon, tcur.tm_mday);
//
//    std::string filepath =  sg_logdir + "/" + forder_name + "/";
//
//    if (!fileUtil::exists(filepath.c_str())) {
//        fileUtil::create_dirs(filepath.c_str());
//    }
//
//    char file_name [128] = {0};
//    snprintf(file_name, sizeof(file_name), "%d%02d%02d%02d%02d%02d_%d.dump", 1900 + tcur.tm_year, 1 + tcur.tm_mon, tcur.tm_mday,
//             tcur.tm_hour, tcur.tm_min, tcur.tm_sec, (int)_len);
//    filepath += file_name;
//
//    FILE* fileid = fopen(filepath.c_str(), "wb");
//
//    if (NULL == fileid) {
//        return "";
//    }
//
//    fwrite(_dumpbuffer, _len, 1, fileid);
//    fclose(fileid);
//
//    char* dump_log = (char*)sg_tss_dumpfile.get();
//    dump_log += snprintf(dump_log, 4096, "\n dump file to %s :\n", filepath.c_str());
//
//    int dump_len = 0;
//
//    for (int x = 0; x < 32 && dump_len < (int)_len; ++x) {
//        dump_log += to_string((const char*)_dumpbuffer + dump_len, std::min(int(_len) - dump_len, 16), dump_log);
//        dump_len += std::min((int)_len - dump_len, 16);
//        *(dump_log++) = '\n';
//    }
//
//    return (const char*)sg_tss_dumpfile.get();
//}
//
//
//static void get_mark_info(char* _info, size_t _infoLen) {
////	struct timeval tv;
////	gettimeofday(&tv, 0);
////	time_t sec = tv.tv_sec;
////	struct tm tm_tmp = *localtime((const time_t*)&sec);
////	char tmp_time[64] = {0};
////	strftime(tmp_time, sizeof(tmp_time), "%Y-%m-%d %z %H:%M:%S", &tm_tmp);
////	snprintf(_info, _infoLen, "[%" PRIdMAX ",%" PRIdMAX "][%s]", xlogger_pid(), xlogger_tid(), tmp_time);
//}
//
//void appender_open(TAppenderMode _mode, const char* _dir, const char* _name_prefix) {
//	assert(_dir);
//	assert(_name_prefix);
//
//    if (!__sg_log_close) {
//        __write_tips_to_file("appender has already been opened. _dir:%s _name_prefix:%s", _dir,
//                             _name_prefix);
//        return
//    }
//
//	//mkdir(_dir, S_IRWXU|S_IRWXG|S_IRWXO);
//	fileUtil::create_dirs(_dir);
//    tickcount_t tick;
//    tick.get_alarm_tick();
//	__del_timeout_file(_dir);
//
//    tickcountdiff_t del_timeout_file_time = tickcount_t().get_alarm_tick() - tick;
//
//    tick.get_alarm_tick();
//
//    char mmap_file_path[512] = {0};
//    snprintf(mmap_file_path, sizeof(mmap_file_path), "%s/%s.mmap2", sg_cache_logdir.empty()?_dir:sg_cache_logdir.c_str(), _name_prefix);
//
//    bool use_mmap = false;
//    if (OpenMmapFile(mmap_file_path, kBufferBlockLength, sg_mmmap_file))  {
//        sg_log_buff = new LogBuffer(sg_mmmap_file.data(), kBufferBlockLength, true);
//        use_mmap = true;
//    } else {
//        char* buffer = new char[kBufferBlockLength];
//        sg_log_buff = new LogBuffer(buffer, kBufferBlockLength, true);
//        use_mmap = false;
//    }
//
//    if (NULL == sg_log_buff->GetData().Ptr()) {
//        if (use_mmap && sg_mmmap_file.is_open())  CloseMmapFile(sg_mmmap_file);
//        return;
//    }
//
//
//    AutoBuffer buffer;
//    sg_log_buff->Flush(buffer);
//
//    std::lock_guard log_file_mutex(sg_mutex_log_file);
//	sg_logdir = _dir;
//	sg_logfileprefix = _name_prefix;
//	__sg_log_close = false;
//    appender_set_mode(_mode);
//    delete log_file_mutex;
//
//    char mark_info[512] = {0};
//    get_mark_info(mark_info, sizeof(mark_info));
//
//    if (buffer.Ptr()) {
//        __write_tips_to_file("~~~~~ begin of mmap ~~~~~\n");
//        __log2file(buffer.Ptr(), buffer.Length());
//        __write_tips_to_file("~~~~~ end of mmap ~~~~~%s\n", mark_info);
//    }
//
//    tickcountdiff_t get_mmap_time = tickcount_t().get_alarm_tick() - tick;
//
//
//    char appender_info[728] = {0};
//    snprintf(appender_info, sizeof(appender_info), "^^^^^^^^^^" __DATE__ "^^^" __TIME__ "^^^^^^^^^^%s", mark_info);
//
//    xlogger_appender(NULL, appender_info);
//    char logmsg[64] = {0};
//    snprintf(logmsg, sizeof(logmsg), "del time out files time: %" PRIu64, (int64_t)del_timeout_file_time);
//    xlogger_appender(NULL, logmsg);
//
//    snprintf(logmsg, sizeof(logmsg), "get mmap time: %" PRIu64, (int64_t)get_mmap_time);
//    xlogger_appender(NULL, logmsg);
//
////    xlogger_appender(NULL, "MARS_URL: " MARS_URL);
////    xlogger_appender(NULL, "MARS_PATH: " MARS_PATH);
////    xlogger_appender(NULL, "MARS_REVISION: " MARS_REVISION);
////    xlogger_appender(NULL, "MARS_BUILD_TIME: " MARS_BUILD_TIME);
////    xlogger_appender(NULL, "MARS_BUILD_JOB: " MARS_TAG);
//
//    snprintf(logmsg, sizeof(logmsg), "log appender mode:%d, use mmap:%d", (int)_mode, use_mmap);
//    xlogger_appender(NULL, logmsg);
//
////	BOOT_RUN_EXIT(appender_close);
//
//}
//
//void appender_open_with_cache(TAppenderMode _mode, const std::string& _cache_dir, const std::string& _log_dir, const char* _name_prefix) {
//    assert(!_cache_dir.empty());
//    assert(!_log_dir.empty());
//    assert(_name_prefix);
//
//    sg_logdir = _log_dir;
//
//    if (!_cache_dir.empty()) {
//    	sg_cache_logdir = _cache_dir;
//    	fileUtil::create_dirs(_cache_dir.c_str());
//    	__del_timeout_file(_cache_dir);
//        // "_name_prefix" must explicitly convert to "std::string", or when the thread is ready to run, "_name_prefix" has been released.
////        Thread(boost::bind(&__move_old_files, _cache_dir, _log_dir, std::string(_name_prefix))).start_after(3 * 60 * 1000);
//    }
//
//    appender_open(_mode, _log_dir.c_str(), _name_prefix);
//
//}
//
//void appender_flush() {
//    sg_cond_buffer_async.notifyAll();
//}
//
//void appender_flush_sync() {
//    if (appenderSync == sg_mode) {
//        return;
//    }
//
//    std::lock_guard<std::recursive_mutex> buffer_async_lock(sg_mutex_buffer_async);
//
//    if (NULL == sg_log_buff) return;
//
//    AutoBuffer tmp;
//    sg_log_buff->Flush(tmp);
//
//    sg_mutex_buffer_async.unlock();
//
//	if (tmp.Ptr())  __log2file(tmp.Ptr(), tmp.Length());
//
//}
//
//void appender_close() {
//    if (__sg_log_close) return;
//
//    char mark_info[512] = {0};
//    get_mark_info(mark_info, sizeof(mark_info));
//    char appender_info[728] = {0};
//    snprintf(appender_info, sizeof(appender_info), "$$$$$$$$$$" __DATE__ "$$$" __TIME__ "$$$$$$$$$$%s\n", mark_info);
//    xlogger_appender(NULL, appender_info);
//
//    __sg_log_close = true;
//
//    sg_cond_buffer_async.notifyAll();
//
//    if (sg_thread_async.isruning())
//        sg_thread_async.join();
//
//    std::lock_guard<std::recursive_mutex> buffer_async_lock(sg_mutex_buffer_async);
//    if (sg_mmmap_file.is_open()) {
//        if (!sg_mmmap_file.operator !()) memset(sg_mmmap_file.data(), 0, kBufferBlockLength);
//
////		CloseMmapFile(sg_mmmap_file);
//    } else {
//        delete[] (char*)((sg_log_buff->GetData()).Ptr());
//    }
//
//    delete sg_log_buff;
//    delete buffer_async_lock;
//
//    std::lock_guard<std::recursive_mutex> lock_file(sg_mutex_log_file);
//    __close_log_file();
//}
//
//void appender_set_mode(TAppenderMode _mode) {
//    sg_mode = _mode;
//
//    sg_cond_buffer_async.notifyAll();
//
//    if (appenderAsync == sg_mode && !sg_thread_async.isruning()) {
//        sg_thread_async.start();
//    }
//}
//
//bool appender_get_current_log_path(char* _log_path, unsigned int _len) {
//    if (NULL == _log_path || 0 == _len) return false;
//
//    if (sg_logdir.empty())  return false;
//
//    strncpy(_log_path, sg_logdir.c_str(), _len - 1);
//    _log_path[_len - 1] = '\0';
//    return true;
//}
//
//bool appender_get_current_log_cache_path(char* _logPath, unsigned int _len) {
//    if (NULL == _logPath || 0 == _len) return false;
//
//    if (sg_cache_logdir.empty())  return false;
//    strncpy(_logPath, sg_cache_logdir.c_str(), _len - 1);
//    _logPath[_len - 1] = '\0';
//    return true;
//}
//
//void appender_set_console_log(bool _is_open) {
//    sg_consolelog_open = _is_open;
//}
//
//void appender_setExtraMSg(const char* _msg, unsigned int _len) {
//    sg_log_extra_msg = std::string(_msg, _len);
//}
//
//bool appender_get_filepath_from_timespan(int _timespan, const char *_prefix,
//                                         std::vector<std::string> &_filepath_vec) {
//    if (sg_logdir.empty()) return false;
//
//    struct timeval tv;
//    gettimeofday(&tv, NULL);
//    tv.tv_sec -= _timespan * (24 * 60 * 60);
//
//    char log_path[2048] = { 0 };
//    __make_log_file_name(tv, sg_logdir, _prefix, LOG_EXT, log_path, sizeof(log_path));
//
//    _filepath_vec.push_back(log_path);
//
//    if (sg_cache_logdir.empty()) {
//        return true;
//    }
//
//    memset(log_path, 0, sizeof(log_path));
//    __make_log_file_name(tv, sg_cache_logdir, _prefix, LOG_EXT, log_path, sizeof(log_path));
//
//    _filepath_vec.push_back(log_path);
//
//    return true;
//}
