inherits "/lib/app.c";
inherits "/lib/list/list.c";
inherits "/lib/sys/fs/io.c";
inherits "/lib/sys/fs/paths.c";

int main(string* argv)
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
    return 0;
}
