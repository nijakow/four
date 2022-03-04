inherits "/std/app.c";

void start()
{
    if (length(argv) != 4)
        printf("Argument error!\n");
    else if (argv[1] == "-aG") {
        if (!addtogroup(argv[2], argv[3]))
            printf("Unable to add '%s' to group '%s'!\n", argv[2], argv[3]);
    } else if (argv[1] == "-rG") {
        if (!removefromgroup(argv[2], argv[3]))
            printf("Unable to remove '%s' from group '%s'!\n", argv[2], argv[3]);
    } else {
        printf("usage: usermod -aG user group OR usermod -Rg user group\n");
    }
    exit();
}
