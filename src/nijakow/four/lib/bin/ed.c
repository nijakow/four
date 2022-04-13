inherits "/lib/app.c";
inherits "/lib/list/list.c";
inherits "/lib/sys/fs/io.c";
inherits "/lib/sys/fs/paths.c";

private void save_cb(string text, string path)
{
    // TODO
}

void main(string* argv)
{
    string path;
    string contents;

    for (int i = 1; i < List_Length(argv); i++)
    {
        path = FileSystem_ResolveHere(argv[i]);
        contents = FileSystem_ReadFile(path);
        terminal()->open_editor(this::(path)save_cb, argv[i], contents);
    }
    exit(0);
}
