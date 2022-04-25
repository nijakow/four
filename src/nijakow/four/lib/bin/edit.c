inherits "/lib/app.c";
inherits "/lib/list/list.c";
inherits "/lib/sys/fs/io.c";
inherits "/lib/sys/fs/paths.c";

private void save_cb(string text, string path)
{
    if (text != nil)
    {
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
        {
            if (FileSystem_CreateFile(path))
                contents = "";
            else {
                printf("%s: file does not exist!\n", argv[i]);
                continue;
            }
        }
        terminal()->open_editor(this::(path)save_cb, argv[i], contents);
    }
    exit(0);
}
