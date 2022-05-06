#include "/lib/app.c"
#include "/lib/list/list.c"
#include "/lib/sys/fs/io.c"
#include "/lib/sys/fs/paths.c"

void main(string* argv)
{
    string path;

    for (int i = 1; i < argv.length; i++)
    {
        path = FileSystem_ResolveHere(argv[i]);
        if (!FileSystem_CreateDirectory(path))
            printf("%s: error while creating directory!\n", argv[i]);
    }
    exit(0);
}
