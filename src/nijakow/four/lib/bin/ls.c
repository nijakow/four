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

void do_ls1(string path, string file, bool long_mode)
{
    if (!exists(path)) {
        printf("%s: file not found!\n", path);
    } else {
        if (long_mode) {
            printf("%s %s %s ", rwx3str(stat(path)), strwid(uname(getown(path)), 8), strwid(gname(getgrp(path)), 8));
        }
        printf("%s\n", file);
    }
}

void do_ls(string dir, bool long_mode, bool dir_mode)
{
    list files = ls(dir);
    if (files == nil || dir_mode) {
        do_ls1(dir, basename(dir), long_mode);
    } else {
        foreach (string f : files)
        {
            do_ls1(dir + "/" + f, f, long_mode);
        }
    }
}

void start()
{
    int off        = 1;
    bool long_mode = true;
    bool dir_mode  = false;

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
        else
            break;
        off = off + 1;
    }

    if (length(argv) - off <= 0)
        do_ls(pwd(), long_mode, dir_mode);
    else if (length(argv) >= 2) {
        for (int i = off; i < length(argv); i++)
        {
            string path = resolve(pwd(), argv[i]);
            do_ls(path, long_mode, dir_mode);
        }
    } else {
        printf("Argument error!\n");
    }
    exit();
}
