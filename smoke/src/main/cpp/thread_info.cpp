//
// Created by yanxq on 2017/2/28.
//

#include <unistd.h>
#include <pthread.h>

#ifdef __cplusplus__
extern "C" {
#endif

int get_pid() {
    return getpid();
}

long get_thread_id() {
    return pthread_self();
}

#ifdef __cplusplus__
}
#endif