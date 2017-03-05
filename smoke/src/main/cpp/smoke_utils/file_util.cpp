//
// Created by yanxq on 2017/2/16.
//

#ifndef SMOKE_FILE_UTILS_H
#define SMOKE_FILE_UTILS_H

#endif //SMOKE_FILE_UTILS_H


#include <sys/stat.h>
#include <dirent.h>
#include <stdio.h>
#include <string>
#include <unistd.h>
#include <errno.h>
#include "file_util.h"
#include "../smoke_jni_log.h"


using namespace fileUtil;

static struct stat* __file_stat(const char *file_path) {
    struct stat buf;
    if (stat(file_path,&buf) == 0) {
        return &buf;
    } else {
        return NULL;
    }
}

bool fileUtil::exists(const char *file_path) {
    return __file_stat(file_path) != NULL;
}

long fileUtil::file_size(const char *file_path) {
    struct stat *file_stat = __file_stat(file_path);
    return file_stat->st_size;
}

bool fileUtil::is_dir(const char *file_path) {
    struct stat *file_stat = __file_stat(file_path);
    if (file_stat != NULL) {
        if (S_ISDIR(file_stat->st_mode)) {
            return true;
        }
    }
    return false;
}

bool fileUtil::is_file(const char *file_path) {
    struct stat *file_stat = __file_stat(file_path);
    if (file_stat != NULL) {
        if (!S_ISDIR(file_stat->st_mode)) {
            return true;
        }
    }
    return false;
}

bool fileUtil::remove_file(const char *file_path) {
    if (!exists(file_path)) {
        return true;
    }

    if (is_dir(file_path)) {
        return delete_dir(file_path);
    } else {
        return remove_file(file_path) == 0;
    }
}

bool fileUtil::delete_dir(const char *dir_path) {
    if (!is_dir(dir_path)) {
        return true;
    }

    DIR *dir = opendir(dir_path);
    if (dir == NULL) {
        return false;
    }

    struct dirent *ptr = NULL;
    while ((ptr = readdir(dir)) != NULL) {
        if (strcmp(ptr->d_name, ".") == 0) {
            continue;
        }

        if (strcmp(ptr->d_name, "..") == 0) {
            continue;
        }

        size_t length = snprintf(nullptr,0,"%s/%s",dir_path,ptr->d_name)+1;
        char child_path[length];
        snprintf(child_path,length,"%s/%s",dir_path,ptr->d_name);

        if (is_dir(child_path)) {
            if (!delete_dir(child_path)) {
                return false;
            }
        } else {
            if (remove_file(child_path) == -1) {
                return false;
            }
        }
    }
    closedir(dir);
    return remove_file(dir_path);
}

bool fileUtil::create_dirs(const char *file_path) {
    if (exists(file_path)) {
        if (is_dir(file_path)) {
            return true;
        } else if (!remove_file(file_path)) {
            return false;
        }
    }

    size_t path_size = strlen(file_path);
    char tmp_dir_path[path_size + 1];
    for (size_t i = 0; i < path_size; ++i) {
        tmp_dir_path[i] = file_path[i];

        if (i == (path_size -1) || file_path[i+1] == '\\' || file_path[i+1] == '/') {
            tmp_dir_path[i+1] = '\0';
            if (!exists(tmp_dir_path)) {
                if (mkdir(tmp_dir_path,S_IXUSR|S_IRWXG) != 0) {
                    const char * error = strerror(errno);
                    smoke_jni::console_error(__FUNCTION__,"mk dir error. [%s] %s",tmp_dir_path,error);
                    return false;
                }
            }
        }
    }
    return true;
}

unsigned long fileUtil::last_write_time(const char *file_path) {
    struct stat *file_stat = __file_stat(file_path);
    if (file_stat != NULL) {
        return file_stat->st_mtime;
    }
    return 0;
}

int fileUtil::find_dir_child_files(const char *dir_path,int max_count,std::string **child_paths) {
    if (!is_dir(dir_path)) {
        return 0;
    }

    DIR *dir = opendir(dir_path);
    if (dir == NULL) {
        return 0;
    }

    int child_file_count = 0;
    struct dirent *ptr = NULL;
    while ((ptr = readdir(dir)) != NULL && child_file_count < max_count) {
        if (strcmp(ptr->d_name, ".") == 0) {
            continue;
        }

        if (strcmp(ptr->d_name, "..") == 0) {
            continue;
        }

        if (child_file_count++ < max_count) {
            std::string *child_path_str = new std::string(ptr->d_name);
            *child_paths++ = child_path_str;
        }
    }
    closedir(dir);
    return child_file_count;
}


