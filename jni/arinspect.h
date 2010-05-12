#ifndef _ARINSPECT_H
#define _ARINSPECT_H

#include <sys/types.h>

typedef struct _arinspect_entry {
    char* pathname;
    char* file_type;
    int64_t size;
    time_t modtime;
    struct _arinspect_entry* next;
} arinspect_entry_t;

arinspect_entry_t* arinspect_entries(const char* filename);
void arinspect_free_list(arinspect_entry_t*);

#endif
