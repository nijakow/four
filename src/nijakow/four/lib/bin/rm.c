inherits "/std/app.c";

void start()
{
    if (length(argv) <= 1)
        printf("Argument error!\n");
    else {
        for (int i = 1; i < length(argv); i++) {
           if (!rm(resolve(pwd(), argv[i])))
                printf("%s: error.\n", argv[i]);
        }
    }
    exit();
}
