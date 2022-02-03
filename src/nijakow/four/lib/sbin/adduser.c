inherits "/std/app.c";

void start()
{
    if (length(argv) != 2)
        printf("Argument error!\n");
    else if (!adduser(argv[1]))
        printf("Unable to create user '%s'!\n", argv[1]);
    exit();
}
