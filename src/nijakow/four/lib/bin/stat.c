inherits "/std/cli.c";

void start(list argv)
{
    if (length(argv) < 2)
        connection()->write("Argument error!\n");
    else {
        for (int i = 1; i < length(argv); i++)
        {
            string path = resolve(pwd(), argv[i]);
            string owner = getown(path);
            if (owner != nil)
                connection()->write(uname(getown(path)), "\n");
            else
                connection()->write("ID not found!\n");
        }
    }
    exit();
}
