#include <errno.h>
#include <string.h>
#include <stdio.h>

#include "arinspect.h"
#include "net_subjoin_mosd_ArchiveInspector.h"

#define UNUSED __attribute__((unused))

#define FILE_DISTRIBUTION_CLASS "Lnet/subjoin/mosd/DistributionFile;"

typedef struct {
    JNIEnv *jenv;
    int failed;
} closure_t;

static void raise_exception_with_message(JNIEnv *jenv,
    const char* className, const char* message)
{
    jclass cls = (*jenv)->FindClass(jenv, className);
    if (!cls) {
        cls = (*jenv)->FindClass(jenv, "Ljava/lang/ClassNotFoundException;");
    }
    (*jenv)->ThrowNew(jenv, cls, message);
}

static void raise_exception(int archive_errno,
    const char* error_string, void* v_closure)
{
    closure_t* closure = (closure_t*)v_closure;
    closure->failed = 1;

    const char* exceptionClassName;
    if (archive_errno == ENOENT) {
        exceptionClassName = "Ljava/io/FileNotFoundException;";
    } else {
        exceptionClassName = "Lnet/subjoin/mosd/ArchiveInspectorException;";
    }
    raise_exception_with_message(closure->jenv,
        exceptionClassName,
        error_string);
}

/*
 * Class:     net_subjoin_mosd_ArchiveInspector
 * Method:    getContents
 * Signature: (Ljava/lang/String;)[Lnet/subjoin/mosd/DistributionFile;
 */
JNIEXPORT jobject JNICALL Java_net_subjoin_mosd_ArchiveInspector_getContents
  (JNIEnv *jenv, jobject UNUSED o, jstring jPath)
{
    arinspect_entry_t *first_entry, *entry;

    closure_t closure = { jenv, 0 };
    const char* path_string = (*jenv)->GetStringUTFChars(jenv,
        jPath, NULL);
    first_entry = arinspect_entries(path_string,
        raise_exception, (void*)&closure);
    (*jenv)->ReleaseStringUTFChars(jenv, jPath, path_string);

    if (closure.failed)
        return NULL;

    int entry_count = 0;
    entry = first_entry;
    while (entry != NULL) {
        entry_count++;
        entry = entry->next;
    }

    jclass clsDistributionFile = (*jenv)->FindClass(jenv,
        FILE_DISTRIBUTION_CLASS);
    if (clsDistributionFile == NULL) {
        jclass clsNotFoundException = (*jenv)->FindClass(jenv,
            "Ljava/lang/ClassNotFoundException;");
        (*jenv)->ThrowNew(jenv, clsNotFoundException,
            FILE_DISTRIBUTION_CLASS);
        return NULL;
    }

    jmethodID midDistributionFileConstructor = (*jenv)->GetMethodID(jenv,
        clsDistributionFile, "<init>",
        "(Ljava/lang/String;Ljava/lang/String;J)V");
    if (midDistributionFileConstructor == NULL) {
        jclass clsNoSuchMethodException = (*jenv)->FindClass(jenv,
            "Ljava/lang/NoSuchMethodException;");
        (*jenv)->ThrowNew(jenv, clsNoSuchMethodException,
            "constructor ()V in " FILE_DISTRIBUTION_CLASS);
        return NULL;
    }

    jobjectArray r = (*jenv)->NewObjectArray(jenv, entry_count,
        clsDistributionFile, NULL);
    entry = first_entry;
    int i;
    for (i = 0; i < entry_count; i++) {
        jobject f = (*jenv)->NewObject(jenv, clsDistributionFile,
            midDistributionFileConstructor,
            /* base, path, size */
            NULL, (*jenv)->NewStringUTF(jenv, entry->pathname), entry->size);
        (*jenv)->SetObjectArrayElement(jenv, r, i, f);
        entry = entry->next;
    }
    arinspect_free_list(first_entry);
    return r;
}
