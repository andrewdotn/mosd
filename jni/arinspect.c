#include <err.h>
#include <errno.h>
#include <stdlib.h>
#include <string.h>

#include <archive.h>
#include <archive_Entry.h>
#include <magic.h>

#include "arinspect.h"

#define MAGIC_BUFFER_SIZE 4096

void arinspect_free_list(arinspect_entry_t* entry) {
    arinspect_entry_t* next;
    while (entry != NULL) {
        free(entry->pathname);
        free(entry->file_type);
        next = entry->next;
        free(entry);
        entry = next;
    }
}

static magic_t magic_cookie = NULL;
char buf[MAGIC_BUFFER_SIZE];

arinspect_entry_t* new_entry(arinspect_entry_t* old)
{
    arinspect_entry_t* r
        = (arinspect_entry_t*)malloc(sizeof(arinspect_entry_t));
    r->next = NULL;
    r->file_type = NULL;
    if (old != NULL)
        old->next = r;
    return r;
}

static arinspect_entry_t*
call_error_handler(
    struct archive *a,
    arinspect_error_handler_t error_handler,
    const char* filename, void* closure_for_error_handler)
{
    int ar_errno = archive_errno(a);
    const char* message = archive_error_string(a);
    if (error_handler) {
        error_handler(ar_errno, message, closure_for_error_handler);
    } else {
        /* The ENOENT error message includes the file name. */
        if (ar_errno != ENOENT)
            warnx("%s: %s", filename, message);
        else
            warnx("%s", message);
    }
    return NULL;
}

arinspect_entry_t*
arinspect_entries(const char* filename,
    arinspect_error_handler_t error_handler,
    void* closure_for_error_handler)
{
    struct archive *a;
    struct archive_entry *a_entry;
    arinspect_entry_t *entry, *first_entry;
    int r;

    if (magic_cookie == NULL) {
        magic_cookie = magic_open(MAGIC_COMPRESS);
        magic_load(magic_cookie, NULL);
    }

    a = archive_read_new();
    archive_read_support_compression_all(a);
    archive_read_support_format_all(a);
    r = archive_read_open_filename(a, filename, 4096);
    if (r != ARCHIVE_OK)
        return call_error_handler(a, error_handler,
            filename, closure_for_error_handler);

    entry = NULL;
    first_entry = NULL;
    while (r = archive_read_next_header(a, &a_entry), r != ARCHIVE_EOF) {
        if (r != ARCHIVE_OK) {
           return call_error_handler(a, error_handler,
               filename, closure_for_error_handler);
        }

        if (archive_entry_filetype(a_entry) != AE_IFREG)
            continue;

        entry = new_entry(entry);
        if (first_entry == NULL)
            first_entry = entry;

        entry->pathname = strdup(archive_entry_pathname(a_entry));
        entry->size = archive_entry_size(a_entry);
        entry->modtime = archive_entry_mtime(a_entry);

        archive_read_data_skip(a);

#if 0
        int size = archive_read_data(a, buf, MAGIC_BUFFER_SIZE);
        entry->file_type = strdup(magic_buffer(magic_cookie, buf, size));
#endif
    }
    r = archive_read_finish(a);
    if (r != ARCHIVE_OK)
        return call_error_handler(a, error_handler,
            filename, closure_for_error_handler);

    return first_entry;
}
