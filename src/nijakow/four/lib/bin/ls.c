inherits "/lib/app.c";
inherits "/lib/list/list.c";
inherits "/lib/sys/fs/io.c";
inherits "/lib/sys/fs/paths.c";
inherits "/lib/sys/fs/permissions.c";
inherits "/lib/sys/fs/stat.c";

private string rwxstr(int flags)
{
    string s = "";
    if (flags & 0x04) s = s + "r";
    else              s = s + "-";
    if (flags & 0x02) s = s + "w";
    else              s = s + "-";
    if (flags & 0x01) s = s + "x";
    else              s = s + "-";
    return s;
}

private string rwx3str(int flags)
{
    string head = "";
    string tail = "";
    if ((flags >> 10) & 0x01) tail = tail + "*";
    else                      tail = tail + " ";
    if ((flags >>  9) & 0x01) head = head + "d";
    else                      head = head + "-";
    return head + rwxstr(flags >> 6) + rwxstr(flags >> 3) + rwxstr(flags) + tail;
}

private void do_ls1(string path, bool long_mode)
{
    if (long_mode) {
        printf("%s %s %s    ", rwx3str(FileSystem_Stat(path)), FileSystem_GetFileOwner(path), FileSystem_GetFileGroup(path));
    }
    printf("%s\n", FileSystem_Basename(path));
}

private void do_ls(string dir, string name, bool long_mode, bool dir_mode, bool recursive)
{
    if (!FileSystem_Exists(dir)) {
        printf("%s: file not found!\n", name);
        return;
    }

    if (!FileSystem_IsDirectory(dir)) dir_mode = true;

    if (dir_mode) {
        do_ls1(dir, long_mode);
        return;
    }

    list files = FileSystem_GetFilesIn(dir);
    if (files == nil) {
        printf("%s: permission denied!\n", name);
        return;
    }

    foreach (string f : files)
    {
        do_ls1(dir + "/" + f, long_mode);
    }
    if (recursive) {
        string full;
        foreach (string f : files) {
            full = (dir == "/") ? dir + f : dir + "/" + f;
            if (FileSystem_IsDirectory(full)) {
                printf("\n%s:\n", full);
                do_ls(full, name, long_mode, dir_mode, recursive);
            }
        }
    }
}

int main(string* argv)
{
    int off        = 1;
    bool long_mode = true;
    bool dir_mode  = false;
    bool recursive = false;

    while (off < List_Length(argv))
    {
        if (argv[off] == "-l")
            long_mode = true;
        else if (argv[off] == "-s")
            long_mode = false;
        else if (argv[off] == "-d")
            dir_mode = true;
        else if (argv[off] == "-ld")
        {
            long_mode = true;
            dir_mode  = true;
        }
        else if (argv[off] == "-sd")
        {
            long_mode = false;
            dir_mode  = true;
        }
        else if (argv[off] == "-R")
            recursive = true;
        else if (argv[off] == "-lR")
        {
            recursive = true;
            long_mode = true;
        }
        else if (argv[off] == "-sR")
        {
            recursive = true;
            long_mode = false;
        }
        else
            break;
        off = off + 1;
    }

    if (List_Length(argv) - off <= 0)
        do_ls(FileSystem_GetWorkingDirectory(), ".", long_mode, dir_mode, recursive);
    else if (List_Length(argv) >= 2) {
        for (int i = off; i < List_Length(argv); i++)
        {
            string path = FileSystem_ResolveHere(argv[i]);
            do_ls(path, argv[i], long_mode, dir_mode, recursive);
            if (i < List_Length(argv) - 1)
                printf("\n");
        }
    } else {
        printf("Argument error!\n");
    }
    return 0;
}
