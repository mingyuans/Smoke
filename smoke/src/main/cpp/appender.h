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
//#ifndef APPENDER_H_
//#define APPENDER_H_
//
//#include <string>
//#include <vector>
//
//enum TAppenderMode {
//    appenderAsync,
//    appenderSync,
//};
//
//void appender_open(TAppenderMode _mode, const char* _dir, const char* _name_prefix);
//void appender_open_with_cache(TAppenderMode _mode, const std::string& _cache_dir, const std::string& _log_dir, const char* _name_prefix);
//void appender_flush();
//void appender_flush_sync();
//void appender_close();
//void appender_set_mode(TAppenderMode _mode);
//bool appender_get_filepath_from_timespan(int _timespan, const char *_prefix,
//                                         std::vector<std::string> &_filepath_vec);
//bool appender_get_current_log_path(char* _log_path, unsigned int _len);
//bool appender_get_current_log_cache_path(char* _logPath, unsigned int _len);
//
//
//#endif /* APPENDER_H_ */
