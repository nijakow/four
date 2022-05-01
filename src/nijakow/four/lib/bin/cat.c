#include "/lib/app.c"
#include "/lib/list/list.c"
#include "/lib/sys/fs/io.c"
#include "/lib/sys/fs/paths.c"

void main(string* argv)
{
    string path;
    string contents;

    for (int i = 1; i < List_Length(argv); i++)
    {
        path = FileSystem_ResolveHere(argv[i]);
        contents = FileSystem_ReadFile(path);
        if (contents == nil)
            printf("%s: not found!\n", argv[i]);
        else {
            printf("%s", contents);
        }
    }
    exit(0);
}
