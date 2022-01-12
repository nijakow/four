inherit "/std/cli.c";

void start(list argv)
{
    if (length(argv) < 2)
        connection()->write("Argument error!\n");
    else {
        for (int i = 1; i < length(argv); i++)
        {
            string path = resolve(pwd(), argv[i]);
            if (path == nil)
                connection()->write(argv[i], ": File not found!\n");
            else if (is_dir(path))
                connection()->write(argv[i], ": Directory\n");
            else if (is_file(path))
                connection()->write(argv[i], ": File\n");
            else
                connection()->write(argv[i], ": File not found!\n");
        }
    }
    exit();
}

void create(object connection, func finish_cb)
{
    "/std/cli.c"::create(connection, finish_cb);
}
