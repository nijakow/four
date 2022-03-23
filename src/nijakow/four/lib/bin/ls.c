inherits "/std/app.c";

string rwxstr(int flags)
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

string rwx3str(int flags)
{
    string head = "";
    string tail = "";
    if ((flags >> 10) & 0x01) tail = tail + "*";
    else                      tail = tail + " ";
    if ((flags >>  9) & 0x01) head = head + "d";
    else                      head = head + "-";
    return head + rwxstr(flags >> 6) + rwxstr(flags >> 3) + rwxstr(flags) + tail;
}

void do_ls1(string path, bool long_mode)
{
    if (long_mode) {
        printf("%s %s %s    ", rwx3str(stat(path)), strwid(uname(getown(path)), 8), strwid(gname(getgrp(path)), 8));
    }
    printf("%s\n", basename(path));
}

void do_ls(string dir, string name, bool long_mode, bool dir_mode, bool recursive)
{
    if (!exists(dir)) {
        printf("%s: file not found!\n", name);
        return;
    }

    if (!is_dir(dir)) dir_mode = true;

    if (dir_mode) {
        do_ls1(dir, long_mode);
        return;
    }

    list files = ls(dir);
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
            if (is_dir(full)) {
                printf("\n%s:\n", full);
                do_ls(full, name, long_mode, dir_mode, recursive);
            }
        }
    }
}

void start()
{
    int off        = 1;
    bool long_mode = true;
    bool dir_mode  = false;
    bool recursive = false;

    while (off < length(argv))
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

    if (length(argv) - off <= 0)
        do_ls(pwd(), ".", long_mode, dir_mode, recursive);
    else if (length(argv) >= 2) {
        for (int i = off; i < length(argv); i++)
        {
            string path = resolve(pwd(), argv[i]);
            do_ls(path, argv[i], long_mode, dir_mode, recursive);
            if (i < length(argv) - 1)
                printf("\n");
        }
    } else {
        printf("Argument error!\n");
    }
    exit();
}
