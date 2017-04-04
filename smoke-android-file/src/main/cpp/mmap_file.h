//
// Created by yanxq on 2017/2/23.
//

#include <cstdio>

#ifndef SMOKE_MMAP_FILE_H
#define SMOKE_MMAP_FILE_H


void *open_mmap(const char *_path, size_t length);

bool close_mmap(void *mmap_ptr);

#endif //SMOKE_MMAP_FILE_H
