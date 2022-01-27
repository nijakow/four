inherits "/std/cli.c";

void start(list argv)
{
    if (length(argv) < 3)
        connection()->write("Argument error!\n");
    else {
        object target = the(argv[1]);
        if (target == nil)
            connection()->write("Could not find the target!\n");
        else
        {
            for (int i = 2; i < length(argv); i++)
            {
                string path = resolve(pwd(), argv[i]);
                connection()->write("Instantiating ", path, "...\n");
                if (path != nil)
                {
                    object obj = new(path);
                    obj->move_to(target);
                }
            }
        }
    }
    exit();
}
