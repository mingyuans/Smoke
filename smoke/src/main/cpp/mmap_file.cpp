//
// Created by yanxq on 2017/2/23.
//

#include "mmap_file.h"
#include <sys/mman.h>
#include <fcntl.h>
#include "smoke_jni_log.h"
#include <cerrno>

static int __mmap_fp = -1;

static size_t __mmap_length = 0;

static void * __mmap_ptr = NULL;

void *open_mmap(const char *_path, size_t length) {
    if (__mmap_ptr != NULL) {
        smoke_jni::console_warn(__FUNCTION__,"mmap file already opended. %s",_path);
        return __mmap_ptr;
    }

    int fp = open(_path,O_RDWR|O_CREAT);
    if (fp == -1) {
        smoke_jni::console_error(__FUNCTION__,"open mmap error. %s [%s]",_path,strerror(errno));
        return NULL;
    }

    //Stretch the file size to fit the size of mmap file.
    int result = lseek(fp,length,SEEK_SET);
    if (result == -1) {
        close(fp);
        smoke_jni::console_error(__FUNCTION__,"lseek mmap file error. %s",strerror(errno));
        return NULL;
    }

    result = write(fp,"",1);
    if (result == -1) {
        close(fp);
        smoke_jni::console_error(__FUNCTION__,"lseek mmap file error. %s",strerror(errno));
        return NULL;
    }

    void * mmap_ptr = mmap(NULL, length, PROT_READ | PROT_WRITE, MAP_SHARED, fp, 0);
    if (mmap_ptr == MAP_FAILED) {
        close(fp);
        smoke_jni::console_error(__FUNCTION__,"open mmap file error. %s",strerror(errno));
        return NULL;
    }
    __mmap_fp = fp;
    __mmap_ptr = mmap_ptr;
    return mmap_ptr;
}

bool close_mmap(void *mmap_ptr) {
    if (munmap(mmap_ptr,__mmap_length) == -1) {
        smoke_jni::console_error(__FUNCTION__,"un-mmap file error. %s",strerror(errno));
    }

    if (__mmap_fp != -1) {
        close(__mmap_fp);
    }
    return true;
}