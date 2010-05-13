#include <err.h>
#include <errno.h>
#include <stdlib.h>
#include <string.h>

#include <archive.h>
#include <archive_Entry.h>

#include "arinspect.h"

#define READ_BUFFER_SIZE 4096

void arinspect_free_list(arinspect_entry_t* entry) {
    arinspect_entry_t* next;
    while (entry != NULL) {
        free(entry->pathname);
        free(entry->file_type);
        if (entry->children) {
            arinspect_free_list(entry->children);
            entry->children = NULL;
        }
        next = entry->next;
        free(entry);
        entry = next;
    }
}

arinspect_entry_t* new_entry(arinspect_entry_t* old)
{
    arinspect_entry_t* r
        = (arinspect_entry_t*)malloc(sizeof(arinspect_entry_t));
    r->next = NULL;
    r->file_type = NULL;
    r->children = NULL;
    if (old != NULL)
        old->next = r;
    return r;
}

static int shouldLookInside1(char* pathname) {
    char* lastDot = strrchr(pathname, '.');
    if (!lastDot)
        return 0;
    char* extension = lastDot;
    if (!strcmp(extension, ".lzma") || !strcmp(extension, ".tgz")
        || !strcmp(extension, ".tar") || !strcmp(extension, ".zip"))
        return 1;
    char c = *extension;
    *extension = '\0';
    char *secondLastDot = strrchr(pathname, '.');
    *extension = c;
    if (!secondLastDot)
        return 0;
    if (!strcmp(secondLastDot, ".tar.gz") || !strcmp(secondLastDot, ".tar.bz2"))
        return 1;
    return 0;
}

/* For unit tests only. */
int shouldLookInside(const char* inPathname) {
    /* The inspection modifies the pathname. Instead of violating constness
     * we make a private copy and modify that. */
    char* pathname = strdup(inPathname);
    int r = shouldLookInside1(pathname);
    free(pathname);
    return r;
}

static void
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
}

static
int
arinspect_entries1(const char* filename, void* buffer, size_t bufsize,
    arinspect_entry_t** entries, arinspect_error_handler_t error_handler,
    void* closure_for_error_handler)
{
    struct archive *a;
    struct archive_entry *a_entry;
    arinspect_entry_t *entry, *first_entry;
    int r;

    a = archive_read_new();
    archive_read_support_compression_all(a);
    archive_read_support_format_all(a);

    if (filename) {
        r = archive_read_open_filename(a, filename, READ_BUFFER_SIZE);
        if (r != ARCHIVE_OK) {
            call_error_handler(a, error_handler,
                filename, closure_for_error_handler);
            return ARINSPECT_ERROR;
        }
    } else {
        r = archive_read_open_memory(a, buffer, bufsize);
        if (r != ARCHIVE_OK) {
            call_error_handler(a, error_handler,
                filename, closure_for_error_handler);
            return ARINSPECT_ERROR;
        }
    }

    entry = NULL;
    first_entry = NULL;
    while (r = archive_read_next_header(a, &a_entry), r != ARCHIVE_EOF) {
        if (r != ARCHIVE_OK) {
            call_error_handler(a, error_handler,
                filename, closure_for_error_handler);
            return ARINSPECT_ERROR;
        }

        if (archive_entry_filetype(a_entry) != AE_IFREG)
            continue;

        entry = new_entry(entry);
        if (first_entry == NULL)
            first_entry = entry;

        entry->pathname = strdup(archive_entry_pathname(a_entry));
        entry->size = archive_entry_size(a_entry);
        entry->modtime = archive_entry_mtime(a_entry);

        if (shouldLookInside(entry->pathname)) {
            char buf[100];
            buffer = malloc(entry->size);
            if (!buffer) {
                sprintf(buf, "Failed to allocated %lld bytes",
                    entry->size);
                archive_set_error(a, ENOMEM, buf);
                call_error_handler(a, error_handler,
                    filename, closure_for_error_handler);
                return ARINSPECT_ERROR;
            }
            int64_t readBytes = archive_read_data(a, buffer, entry->size);
            if (readBytes != entry->size) {
                sprintf(buf,
                    "archive_read_data returned %lld bytes (%lld expected)",
                    readBytes, entry->size);
                archive_set_error(a, EIO, buf);
                call_error_handler(a, error_handler,
                    filename, closure_for_error_handler);
                return ARINSPECT_ERROR;
            }
            arinspect_entry_t *child_entries;
            r = arinspect_entries1(NULL, buffer, entry->size, &child_entries,
                error_handler, closure_for_error_handler);
            if (r != ARINSPECT_OK)
                return r;

            entry->children = child_entries;
        }

        archive_read_data_skip(a);
    }
    r = archive_read_finish(a);
    if (r != ARCHIVE_OK) {
        call_error_handler(a, error_handler,
            filename, closure_for_error_handler);
        return ARINSPECT_ERROR;
    }

    *entries = first_entry;
    return ARINSPECT_OK;
}

arinspect_entry_t*
arinspect_entries(const char* filename,
    arinspect_error_handler_t error_handler,
    void* closure_for_error_handler)
{
    arinspect_entry_t* entry = NULL;
    int r = arinspect_entries1(filename, NULL, 0,
        &entry, error_handler, closure_for_error_handler);
    if (r == ARINSPECT_OK)
        return entry;
    return NULL;
}
