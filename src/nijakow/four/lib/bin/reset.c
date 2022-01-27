inherits "/std/cli.c";

void start(list argv)
{
    if (length(argv) < 2)
        connection()->write("Argument error!\n");
    else {
        for (int i = 1; i < length(argv); i++)
        {
            string path = resolve(pwd(), argv[i]);
            connection()->write("Resetting ", path, "...\n");
            if (path != nil)
            {
                object obj = the(path);
                connection()->write(" - ", obj, "...\n");
                if (obj != nil) {
                    obj->create();
                }
            }
        }
    }
    exit();
}
