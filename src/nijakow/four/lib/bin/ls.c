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

void do_ls1(string base, string file)
{
    connection()->write(file, "\n");
}

void do_ls(string dir)
{
    list files = ls(dir);
    if (files == nil) {
        connection()->write(dir, ": Not a directory!\n");
    } else {
        foreach (string f : files)
        {
            do_ls1(dir, f);
        }
    }
}

void start(list argv)
{
    if (length(argv) == 1)
        do_ls(pwd());
    else if (length(argv) >= 2) {
        for (int i = 1; i < length(argv); i++)
        {
            string path = resolve(pwd(), argv[i]);
            do_ls(path);
        }
    } else {
        connection()->write("Argument error!\n");
    }
    exit();
}

void create(object connection, func finish_cb)
{
    "/std/cli.c"::create(connection, finish_cb);
}
