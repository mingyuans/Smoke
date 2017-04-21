//
// Created by yanxq on 2017/2/18.
//

#include "smoke_jni_test.h"
#include "../smoke_utils/file_util.h"
#include "../smoke_jni_log.h"
#include "../smoke_appender.h"
#include <string>

# define ARRAY_LEN(x) ((int) (sizeof(x) / sizeof((x)[0])))

using namespace std;
string __package_name="com/mingyuans/smoke";

static bool __jni_is_dir(JNIEnv *env, jobject thiz, jstring _file_path) {
    const char * file_path = env->GetStringUTFChars(_file_path,0);
    bool result = fileUtil::is_dir(file_path);
    env->ReleaseStringUTFChars(_file_path,file_path);
    return result;
}

static bool __jni_mk_dir(JNIEnv *jniEnv,jobject thiz, jstring _file_path) {
    const char * file_path = jniEnv->GetStringUTFChars(_file_path,0);
    bool result = fileUtil::create_dirs(file_path);
    jniEnv->ReleaseStringUTFChars(_file_path,file_path);
    return result;
}

static bool __jni_delete_dir(JNIEnv *jniEnv, jobject thiz, jstring _file_path) {
    const char * file_path = jniEnv->GetStringUTFChars(_file_path,0);
    bool result = fileUtil::delete_dir(file_path);
    jniEnv->ReleaseStringUTFChars(_file_path,file_path);
    return result;
}

static long __jni_last_modify_time(JNIEnv *env, jobject object, jstring _file_path) {
    const char * file_path = env->GetStringUTFChars(_file_path,0);
    bool result = fileUtil::last_write_time(file_path);
    env->ReleaseStringUTFChars(_file_path,file_path);
    return result;
}

static jobjectArray __jni_find_dir_files(JNIEnv *env, jobject object,jstring _file_path) {
    const  char *file_path = env->GetStringUTFChars(_file_path,0);
    std::string **child_files = new std::string*[100];
    int count = fileUtil::find_dir_child_files(file_path,100,child_files);
    jclass stringClazz = env->FindClass("java/lang/String");
    jobjectArray result = env->NewObjectArray(count,stringClazz,NULL);
    for (int i = 0; i < count; ++i) {
        std::string *child = child_files[i];
        jstring child_str = env->NewStringUTF(child->c_str());
        env->SetObjectArrayElement(result,i,child_str);
        delete child;
    }
    delete child_files;
    env->ReleaseStringUTFChars(_file_path,file_path);
    return result;
}

static void __jni_make_native_crash() {
    char *null_array = NULL;
    char null_char = *null_array;
}

static void __register_file_util_test(JNIEnv *env) {
    JNINativeMethod nativeMethods[] = {
            {"is_directory","(Ljava/lang/String;)Z",(void *)__jni_is_dir},
            {"mk_dir","(Ljava/lang/String;)Z",(void *)__jni_mk_dir},
            {"delete_dir","(Ljava/lang/String;)Z",(void *)__jni_delete_dir},
            {"last_modify_time","(Ljava/lang/String;)J",(void *)__jni_last_modify_time},
            {"find_dir_files","(Ljava/lang/String;)[Ljava/lang/String;",(void *)__jni_find_dir_files},
            {"make_native_crash","()V",(void *)__jni_make_native_crash},
    };
    string class_name = __package_name + "/JniFileUtilTest";
    jclass file_util_test_class = env->FindClass(class_name.c_str());
    if (file_util_test_class != NULL) {
        env->RegisterNatives(file_util_test_class, nativeMethods, ARRAY_LEN(nativeMethods));
    }
}

static void __test_appender_on(JNIEnv *env, jobject object, jstring dir_, jstring cache_dir_,jstring prefixe_) {
    const char *dir = env->GetStringUTFChars(dir_,0);
    const char *cache_dir = env->GetStringUTFChars(cache_dir_,0);
    const char *name_prefix = env->GetStringUTFChars(prefixe_,0);

    appender_open(AppenderMode::MODE_ASYNC,dir,cache_dir,name_prefix,"sm");

    env->ReleaseStringUTFChars(dir_,dir);
    env->ReleaseStringUTFChars(cache_dir_,cache_dir);
    env->ReleaseStringUTFChars(prefixe_,name_prefix);
}

static void __register_smoke_jni_test(JNIEnv *env) {
    JNINativeMethod nativeMethods[] = {
            {"appender_open","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",(void *)__test_appender_on},
    };
    string class_name = __package_name + "/SmokeJniTest";
    jclass file_util_test_class = env->FindClass(class_name.c_str());
    if (file_util_test_class != NULL) {
        env->RegisterNatives(file_util_test_class, nativeMethods, ARRAY_LEN(nativeMethods));
    }
}

void register_test_methods(JNIEnv *env) {
    __register_file_util_test(env);
    __register_smoke_jni_test(env);
}

JNIEXPORT jint
JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv* env = NULL;
    jint result = -1;

    if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
        return result;
    }

    smoke_jni::console_verbose(__FUNCTION__,"jni onLoad");

    register_test_methods(env);


    // 返回jni的版本
    return JNI_VERSION_1_4;
}

