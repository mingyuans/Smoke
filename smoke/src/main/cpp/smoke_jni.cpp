//
// Created by yanxq on 17/2/15.
//

#include <jni.h>
#include "smoke_base.h"
#include "smoke_appender.h"

static bool console_print_enable = false;

extern "C" {

//JNIEXPORT void JNICALL
//Java_com_mingyuans_smoke_DefaultFilePrinter_jniConsoleEnable(JNIEnv *env, jclass type, jboolean enable) {
//    console_print_enable = enable;
//}

JNIEXPORT void JNICALL
Java_com_mingyuans_smoke_SmokeFilePrinter_jniPrintln(JNIEnv *env, jobject instance, jint level, jstring tag_,
                                          jobjectArray message_) {
    const char *tag = env->GetStringUTFChars(tag_, 0);

    int array_length = env->GetArrayLength(message_);
    const char *message_array[array_length];
    for (int i = 0; i < array_length; ++i) {
        jstring one_msg = (jstring) env->GetObjectArrayElement(message_, i);
        const char *message = env->GetStringUTFChars(one_msg, 0);
        message_array[i] = message;
    }

    if (console_print_enable) {
        for (int i = 0; i < array_length; ++i) {
            smoke::_console_println(level, tag, message_array[i]);
        }
    }
    smoke::_write_log_array(level, tag, message_array,array_length);

    env->ReleaseStringUTFChars(tag_, tag);
    for (int i = 0; i < array_length; ++i) {
        jstring one_msg = (jstring) env->GetObjectArrayElement(message_, i);
        env->ReleaseStringUTFChars(one_msg,message_array[i]);
    }
}


JNIEXPORT void JNICALL
Java_com_mingyuans_smoke_SmokeFilePrinter_jniOpen(JNIEnv *env, jobject instance, jint append_mode_,jstring file_dir_,
                                          jstring cache_dir_, jstring name_prefix_) {
    const char *file_dir = env->GetStringUTFChars(file_dir_, 0);
    const char *cache_dir = env->GetStringUTFChars(cache_dir_, 0);
    const char *name_prefix = env->GetStringUTFChars(name_prefix_, 0);

    smoke::_open(append_mode_,file_dir,cache_dir,name_prefix);

    env->ReleaseStringUTFChars(file_dir_, file_dir);
    env->ReleaseStringUTFChars(cache_dir_, cache_dir);
    env->ReleaseStringUTFChars(name_prefix_, name_prefix);
}

JNIEXPORT void JNICALL
Java_com_mingyuans_smoke_SmokeFilePrinter_jniClose(JNIEnv *env, jobject instance) {
    smoke::_close();
}




}
