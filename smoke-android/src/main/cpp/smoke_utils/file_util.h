//
// Created by yanxq on 2017/2/16.
//

#include <string>
#include <vector>

using namespace std;

#ifndef SMOKE_FILE_UTILS_H
#define SMOKE_FILE_UTILS_H


#endif //SMOKE_FILE_UTILS_H


namespace fileUtil {
#ifdef __cplusplus
    extern "C" {
#endif

    bool exists(const char *file_path);

    bool is_dir(const char *file_path);

    bool is_file(const char *file_path);

    bool create_dirs(const char *file_path);

    bool delete_dir(const char *dir_path);

    long file_size(const char *file_path);

    bool remove_file(const char *file_path);

    unsigned long last_write_time(const char *file_path);

#ifdef __cplusplus
    }
#endif

    unsigned long find_dir_child_files(const char *dir_path,vector<string> &child_files);

    int find_dir_child_files(const char *dir_path,int max_count,std::string **child_paths);
}



