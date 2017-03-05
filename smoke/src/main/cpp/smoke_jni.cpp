//
// Created by yanxq on 17/2/15.
//

#include <jni.h>
#include "smoke_base.h"
#include "smoke_appender.h"

static bool console_print_enable = false;

extern "C" {

JNIEXPORT void JNICALL
Java_com_mingyuans_smoke_SmokeSub_jniConsoleEnable(JNIEnv *env, jclass type, jboolean enable) {
    console_print_enable = enable;
}

JNIEXPORT void JNICALL
Java_com_mingyuans_smoke_SmokeSub_jniPrintln(JNIEnv *env, jobject instance, jint level, jstring tag_,
                                          jstring message_) {
    const char *tag = env->GetStringUTFChars(tag_, 0);
    const char *message = env->GetStringUTFChars(message_, 0);

    if (console_print_enable) {
        smoke::_console_println(level, tag, message);
    }
    smoke::_write_log(level, tag, message);

    env->ReleaseStringUTFChars(tag_, tag);
    env->ReleaseStringUTFChars(message_, message);
}


JNIEXPORT void JNICALL
Java_com_mingyuans_smoke_SmokeSub_jniOpen(JNIEnv *env, jobject instance, jstring file_dir_,
                                          jstring cache_dir_, jstring name_prefix_) {
    const char *file_dir = env->GetStringUTFChars(file_dir_, 0);
    const char *cache_dir = env->GetStringUTFChars(cache_dir_, 0);
    const char *name_prefix = env->GetStringUTFChars(name_prefix_, 0);

    smoke::_open(file_dir,cache_dir,name_prefix);

    env->ReleaseStringUTFChars(file_dir_, file_dir);
    env->ReleaseStringUTFChars(cache_dir_, cache_dir);
    env->ReleaseStringUTFChars(name_prefix_, name_prefix);
}

JNIEXPORT void JNICALL
Java_com_mingyuans_smoke_SmokeSub_jniClose(JNIEnv *env, jobject instance) {
    smoke::_close();
}




}
