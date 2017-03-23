# Sets the minimum version of CMake required to build the native
# library. You should either keep the default value or only pass a
# value of 3.4.0 or lower.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds it for you.
# Gradle automatically packages shared libraries with your APK.

set(SMOKE_SOURCE_FILES
    src/main/cpp/log_formater.cpp
    src/main/cpp/thread_info.cpp
    src/main/cpp/crypt/log_crypt.cpp
    src/main/cpp/tickcount.cpp
    src/main/cpp/bootrun.h
    src/main/cpp/smoke_utils/compiler_util.h
    src/main/cpp/smoke_utils/time_utils.c
    src/main/cpp/smoke_utils/file_util.cpp
    src/main/cpp/smoke_jni_log.cpp
    src/main/cpp/smoke_log.cpp
    src/main/cpp/mmap_file.cpp
    src/main/cpp/log_buffer.cpp
    src/main/cpp/smoke_appender.cpp
    src/main/cpp/ptrbuffer.cpp
    src/main/cpp/autobuffer.cpp
    src/main/cpp/smoke_jni.cpp
    src/main/cpp/smoke_base.cpp)

add_library( # Sets the name of the library.

             smoke-lib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             # Associated headers in the same location as their source
             # file are automatically included.
             ${SMOKE_SOURCE_FILES})
set_target_properties(smoke-lib PROPERTIES LINK_LANGUAGE CXX)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because system libraries are included in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

find_library(z-lib  z)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in the
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
                       smoke-lib

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} ${z-lib} )


set(TEST_SMOKE_SOURCE_FILES
    src/main/cpp/test/smoke_jni_test.cpp)

add_library( smoke-test-lib
             SHARED
             ${TEST_SMOKE_SOURCE_FILES})

target_link_libraries(smoke-test-lib
                      smoke-lib)
