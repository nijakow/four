inherits "/std/app.c";

void start()
{
    if (length(argv) <= 1)
        printf("Argument error!\n");
    else {
        string uid = finduser(argv[1]);
        if (uid == nil)
            printf("User %s not found!\n", argv[1]);
        else {
            for (int i = 2; i < length(argv); i++)
            {
                if (!chown(resolve(pwd(), argv[i]), uid))
                    printf("%s: Can't change owner!\n", argv[i]);
            }
        }
    }
    exit();
}
