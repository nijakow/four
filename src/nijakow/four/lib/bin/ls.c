inherits "/std/cli.c";

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
    return rwxstr(flags >> 6) + rwxstr(flags >> 3) + rwxstr(flags);
}

void do_ls1(string base, string file, bool long_mode)
{
    string full = base + "/" + file;
    if (long_mode) {
        connection()->write(rwx3str(stat(full)), " ",
                            strwid(uname(getown(full)), 8), " ",
                            strwid(gname(getgrp(full)), 8), " ");
    }
    connection()->write(file, "\n");
}

void do_ls(string dir, bool long_mode)
{
    list files = ls(dir);
    if (files == nil) {
        connection()->write(dir, ": Not a directory!\n");
    } else {
        foreach (string f : files)
        {
            do_ls1(dir, f, long_mode);
        }
    }
}

void start(list argv)
{
    int off        = 0;
    bool long_mode = false;

    if (length(argv) > 1 && argv[1] == "-l") {
        off       = 1;
        long_mode = true;
    }

    if (length(argv) - off <= 1)
        do_ls(pwd(), long_mode);
    else if (length(argv) >= 2) {
        for (int i = 1 + off; i < length(argv); i++)
        {
            string path = resolve(pwd(), argv[i]);
            do_ls(path, long_mode);
        }
    } else {
        connection()->write("Argument error!\n");
    }
    exit();
}
