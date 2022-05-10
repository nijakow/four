#include "/lib/app.c"
#include "/lib/sys/fs/paths.c"

use $uncompress;

void main(string* argv)
{
    if (argv.length != 3)
        printf("Usage: %s <file> <dir>\n", argv[0]);
    else {
        string file = FileSystem_ResolveHere(argv[1]);
        string path = FileSystem_ResolveHere(argv[2]);
        if (!$uncompress(file, path))
            printf("%s: error!\n", argv[1]);
    }
    exit(0);
}
