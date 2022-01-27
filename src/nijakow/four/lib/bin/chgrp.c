inherits "/std/app.c";

void start()
{
    if (length(argv) <= 1)
        connection()->write("Argument error!\n");
    else {
        string gid = findgroup(argv[1]);
        if (gid == nil)
            connection()->write("Group " + argv[1] + " not found!\n");
        else {
            for (int i = 2; i < length(argv); i++)
            {
                if (!chgrp(resolve(pwd(), argv[i]), uid))
                    connection()->write(argv[i], ": Can't change group!\n");
            }
        }
    }
    exit();
}
