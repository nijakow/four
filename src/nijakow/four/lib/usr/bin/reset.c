inherits "/std/app.c";

void start()
{
    if (length(argv) < 2)
        printf("Argument error!\n");
    else {
        for (int i = 1; i < length(argv); i++)
        {
            string path = resolve(pwd(), argv[i]);
            printf("Resetting %s...\n", path);
            if (path != nil)
            {
                object obj = the(path);
                printf(" - %s...\n", obj);
                if (obj != nil) {
                    obj->create();
                }
            }
        }
    }
    exit();
}
