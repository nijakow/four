inherits "/std/app.c";

bool make_home(string name)
{
    string dirname = "/home/" + name;

    if (!exists("/home")) mkdir("/home");
    return mkdir(dirname) && chown(dirname, name) && chgrp(dirname, "users") && chmod(dirname, 0744);
}

void start()
{
    if (length(argv) != 2)
        printf("Argument error!\n");
    else if (!adduser(argv[1]))
        printf("Unable to create user '%s'!\n", argv[1]);
    else if (!make_home(argv[1]))
        printf("Unable to create home directory!\n");
    exit();
}
