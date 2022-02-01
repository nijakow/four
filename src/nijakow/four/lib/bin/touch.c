inherits "/std/app.c";

void start()
{
    if (length(argv) <= 1)
        printf("Argument error!\n");
    else {
        for (int x = 1; x < length(argv); x++)
        {
            string path = resolve(pwd(), argv[x]);
            if (path != nil)
            {
                if (!touch(path))
                    printf("Could not create file!\n");
            }
        }
    }
    exit();
}
