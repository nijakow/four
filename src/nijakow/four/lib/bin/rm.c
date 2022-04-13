inherits "/lib/app.c";
inherits "/lib/list/list.c";
inherits "/lib/sys/fs/io.c";
inherits "/lib/sys/fs/paths.c";

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
