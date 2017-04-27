#!/usr/bin/env bash
if [ ! -n "${ANDROID_HOME}" ]; then
    echo "You must define ANDROID_HOME at first!!!"
    exit -1
else
   echo "ANDROID_HOME: ${ANDROID_HOME}"
fi

proguard_retrace_file="${ANDROID_HOME}/tools/proguard/bin/retrace.sh"

if [ ! -f "${proguard_retrace_file}" ]; then
    echo "I can't find ${proguard_retrace_file}!"
    exit -1
else
    echo "Proguard retrace file: ${proguard_retrace_file}"
fi

${proguard_retrace_file} -regex ".*\\[%c.%m\\]\\s*\\[.*\\]\\s*|(?:\\s*%c:.*)|(?:.*at\\s+%c.%m\\s*\\(.*?(?::%l)?\\)\\s*)" $1 $2