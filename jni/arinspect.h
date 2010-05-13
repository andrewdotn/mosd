#ifndef _ARINSPECT_H
#define _ARINSPECT_H

#include <sys/types.h>

#define ARINSPECT_OK 0
#define ARINSPECT_ERROR -1

typedef struct _arinspect_entry {
    char* pathname;
    char* file_type;
    int64_t size;
    time_t modtime;
    struct _arinspect_entry* children;
    struct _arinspect_entry* next;
} arinspect_entry_t;

typedef void arinspect_error_handler_t(int archive_errno,
    const char* error_string, void* closure);

/* If an error occurs, error_handler is called and NULL is returned.
 * If error_handler is NULL, the error is printed to stderr. */
arinspect_entry_t* arinspect_entries(const char* filename,
        arinspect_error_handler_t foo, void* closure_for_error_handler);
void arinspect_free_list(arinspect_entry_t*);

#endif
