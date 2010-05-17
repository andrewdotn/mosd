#include <errno.h>
#include <string.h>
#include <stdio.h>

#include "arinspect.h"
#include "net_subjoin_mosd_ArchiveInspector.h"
#include "net_subjoin_mosd_ArchiveInspectorTest.h"

#define UNUSED __attribute__((unused))

#define FILE_DISTRIBUTION_CLASS "Lnet/subjoin/mosd/DistributionFile;"
#define FILE_DISTRIBUTION_CONSTRUCTOR_SIGNATURE "(Ljava/lang/String;J)V"
#define FILE_DISTRIBUTION_CONSTRUCTOR_SIGNATURE_WITH_CHILDREN \
        "(Ljava/lang/String;J[" FILE_DISTRIBUTION_CLASS ")V"
#define ERROR_HANDLER_CLASS \
         "Lnet/subjoin/mosd/ArchiveInspectorErrorHandler;"
#define ERROR_HANDLER_METHOD_NAME "handleError"
#define ERROR_HANDLER_METHOD_SIG "(Ljava/lang/String;)V"
#define CLASS_NOT_FOUND_EXCEPTION_CLASS "Ljava/lang/ClassNotFoundException;"

typedef struct {
    JNIEnv *jenv;
    jobject realHandler;
    int failed;
} closure_t;

static void raise_exception_with_message(JNIEnv *jenv,
    const char* className, const char* message)
{
    jclass cls = (*jenv)->FindClass(jenv, className);
    if (!cls) {
        cls = (*jenv)->FindClass(jenv, CLASS_NOT_FOUND_EXCEPTION_CLASS);
    }
    (*jenv)->ThrowNew(jenv, cls, message);
}

static int call_error_handler(int archive_errno,
    const char* error_string, void* v_closure)
{
    closure_t* closure = (closure_t*)v_closure;
    JNIEnv* jenv = closure->jenv;

    const char* exceptionClassName;
    if (archive_errno == ENOENT) {
        exceptionClassName = "Ljava/io/FileNotFoundException;";
        raise_exception_with_message(closure->jenv,
            exceptionClassName,
            error_string);
        closure->failed = ARINSPECT_ERROR;
    } else {
        jclass clsErrorHandler = (*jenv)->FindClass(jenv,
            ERROR_HANDLER_CLASS);
        if (!clsErrorHandler) {
            raise_exception_with_message(jenv,
                CLASS_NOT_FOUND_EXCEPTION_CLASS,
                ERROR_HANDLER_CLASS);
            return ARINSPECT_ERROR;
        }

        jmethodID mid = (*jenv)->GetMethodID(jenv, clsErrorHandler,
            ERROR_HANDLER_METHOD_NAME, ERROR_HANDLER_METHOD_SIG);
        if (!mid) {
            raise_exception_with_message(jenv,
                "Ljava/lang/NoSuchMethodException;",
                ERROR_HANDLER_METHOD_NAME ERROR_HANDLER_METHOD_SIG
                " in " ERROR_HANDLER_CLASS);
            return ARINSPECT_ERROR;
        }

        jstring jmessage = (*jenv)->NewStringUTF(jenv, error_string);
        (*jenv)->CallVoidMethod(jenv, closure->realHandler, mid, jmessage);

        if ((*jenv)->ExceptionOccurred(jenv))
            closure->failed = ARINSPECT_ERROR;
    }

    return closure->failed;
}

static jobject buildDistributionFileTree(arinspect_entry_t *first_entry,
    JNIEnv* jenv, jclass clsDistributionFile,
    jmethodID midConstructor, jmethodID midConstructorWithChildren)
{
    int entry_count = 0;
    arinspect_entry_t *entry = first_entry;
    while (entry) {
        entry_count++;
        entry = entry->next;
    }

    jobjectArray r = (*jenv)->NewObjectArray(jenv, entry_count,
        clsDistributionFile, NULL);

    entry = first_entry;
    int i = 0;
    for (i = 0; i < entry_count; i++) {
        jobject f;
        if (entry->children) {
            f = (*jenv)->NewObject(jenv, clsDistributionFile,
                midConstructorWithChildren,
                /* path, size */
                (*jenv)->NewStringUTF(jenv, entry->pathname),
                entry->size,
                /* children */
                buildDistributionFileTree(entry->children, jenv,
                    clsDistributionFile,
                    midConstructor,
                    midConstructorWithChildren));
        } else {
            f = (*jenv)->NewObject(jenv, clsDistributionFile,
                midConstructor,
                /* path, size */
                (*jenv)->NewStringUTF(jenv, entry->pathname), entry->size);
        }

        (*jenv)->SetObjectArrayElement(jenv, r, i, f);
        entry = entry->next;
    }
    return r;
}

/*
 * Class:     net_subjoin_mosd_ArchiveInspector
 * Method:    getContents
 * Signature: (Ljava/lang/String;Lnet/subjoin/mosd/ArchiveInspector/ErrorHandler;)[Lnet/subjoin/mosd/DistributionFile;
 */
JNIEXPORT jobjectArray JNICALL Java_net_subjoin_mosd_ArchiveInspector_getContents
  (JNIEnv *jenv, jclass UNUSED cls, jstring jPath, jobject jErrorHandler)
{
    arinspect_entry_t *first_entry;

    closure_t closure = { jenv, jErrorHandler, ARINSPECT_OK };
    const char* path_string = (*jenv)->GetStringUTFChars(jenv,
        jPath, NULL);
    first_entry = arinspect_entries(path_string,
        call_error_handler, (void*)&closure);
    (*jenv)->ReleaseStringUTFChars(jenv, jPath, path_string);

    if (closure.failed)
        return NULL;

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
        FILE_DISTRIBUTION_CONSTRUCTOR_SIGNATURE);
    if (midDistributionFileConstructor == NULL) {
        jclass clsNoSuchMethodException = (*jenv)->FindClass(jenv,
            "Ljava/lang/NoSuchMethodException;");
        (*jenv)->ThrowNew(jenv, clsNoSuchMethodException,
            "constructor " FILE_DISTRIBUTION_CONSTRUCTOR_SIGNATURE
            " in " FILE_DISTRIBUTION_CLASS);
        return NULL;
    }
    jmethodID midDistributionFileConstructorWithChildren
        = (*jenv)->GetMethodID(jenv,
            clsDistributionFile, "<init>",
            FILE_DISTRIBUTION_CONSTRUCTOR_SIGNATURE_WITH_CHILDREN);
    if (midDistributionFileConstructorWithChildren == NULL) {
        jclass clsNoSuchMethodException = (*jenv)->FindClass(jenv,
            "Ljava/lang/NoSuchMethodException;");
        (*jenv)->ThrowNew(jenv, clsNoSuchMethodException,
            "constructor "
            FILE_DISTRIBUTION_CONSTRUCTOR_SIGNATURE_WITH_CHILDREN
            " in " FILE_DISTRIBUTION_CLASS);
        return NULL;
    }

    jobject r = buildDistributionFileTree(first_entry,
        jenv, clsDistributionFile,
        midDistributionFileConstructor,
        midDistributionFileConstructorWithChildren);
    arinspect_free_list(first_entry);
    return r;
}

/*
 * Class:     net_subjoin_mosd_ArchiveInspectorTest
 * Method:    shouldLookInside
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_net_subjoin_mosd_ArchiveInspectorTest_shouldLookInside
  (JNIEnv *jenv, jclass UNUSED cls, jstring jPath)
{
    const char* path = (*jenv)->GetStringUTFChars(jenv,
        jPath, NULL);
    int r = shouldLookInside(path);
    (*jenv)->ReleaseStringUTFChars(jenv, jPath, path);
    return r;
}
