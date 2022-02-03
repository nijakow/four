inherits "/std/app.c";

void process(string uid, string shell)
{
    if (uid == finduser("root")) {
        printf("It is unsafe to change root's shell!\n");
    } else if (uid == nil) {
        printf("User not found!\n");
    } else if (!chsh(uid, shell)) {
        printf("Could not set shell!\n");
    }
    exit();
}

void start()
{
    if (length(argv) == 2)
        process(getuid(), argv[1]);
    else if (length(argv) == 3)
        process(finduser(argv[1]), argv[2]);
    else
    {
        printf("Argument error!\n");
        exit();
    }
}
