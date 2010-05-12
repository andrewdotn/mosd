#include "net_subjoin_mosd_JNITest.h"

#define UNUSED __attribute__((unused))

JNIEXPORT jstring JNICALL Java_net_subjoin_mosd_JNITest_hello
  (JNIEnv *jenv, jclass UNUSED cls)
{
    return (*jenv)->NewStringUTF(jenv, "Hello from JNI!\n");
}
