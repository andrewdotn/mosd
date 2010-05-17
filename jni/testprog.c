#include <stdio.h>
#include <locale.h>

#include "arinspect.h"

#define UNUSED __attribute__((unused))

static char* filename;

static int error_handler(UNUSED int archive_errno, const char* error_string,
    UNUSED void* closure)
{
    fprintf(stderr, "%s\n", error_string);
    return ARINSPECT_OK;
}

static void print_list(arinspect_entry_t *entry, int depth)
{
    while (entry != NULL) {
        int i;
        for (i = 0; i < depth; i++)
            printf("  ");
        printf("%s %lld %ld\n",
            entry->pathname, entry->size, entry->modtime);
        if (entry->children)
            print_list(entry->children, depth + 1);
        entry = entry->next;
    }
}

int main(int argc, char** argv) {
    (void)setlocale(LC_CTYPE, "");

    int i;
    for (i = 1; i < argc; i++) {
        filename = argv[i];
        arinspect_entry_t* first_entry = arinspect_entries(filename,
            error_handler, NULL);
        print_list(first_entry, 0);
        arinspect_free_list(first_entry);
    }
    return 0;
}
