inherits "/lib/app.c";
inherits "/lib/list/list.c";
inherits "/lib/sys/fs/io.c";
inherits "/lib/sys/fs/paths.c";
inherits "/lib/sys/fs/stat.c";

private void save_cb(string text, string path)
{
    if (text != nil)
    {
        if (!FileSystem_Exists(path)) {
            if (!FileSystem_CreateFile(path)) {
                printf("%s: could not create file!\n", path);
                return;
            }
        }

        if (FileSystem_WriteFile(path, text))
            printf("%s: written!\n", path);
        else
            printf("%s: error when writing file!\n", path);
    }
}

void main(string* argv)
{
    string path;
    string contents;

    for (int i = 1; i < List_Length(argv); i++)
    {
        path = FileSystem_ResolveHere(argv[i]);
        contents = FileSystem_ReadFile(path);
        if (contents == nil)
            contents = "";
        terminal()->open_editor(this::(path)save_cb, argv[i], contents);
    }
    exit(0);
}
