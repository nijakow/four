#include "/lib/app.c"
#include "/lib/list/list.c"
#include "/lib/sys/fs/io.c"
#include "/lib/sys/fs/paths.c"

void main(string* argv)
{
    string path;

    for (int i = 1; i < List_Length(argv); i++)
    {
        path = FileSystem_ResolveHere(argv[i]);
        // TODO: Recursive Delete
        if (!FileSystem_DeleteFile(path))
            printf("%s: error while deleting file!\n", argv[i]);
    }
    exit(0);
}
