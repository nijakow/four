inherits "/std/app.c";

void start()
{
    if (length(argv) < 2)
        printf("Argument error!\n");
    else {
        for (int i = 1; i < length(argv); i++)
        {
            string path = resolve(pwd(), argv[i]);
            if (path == nil)
                printf("%s: File not found!\n", argv[i]);
            else if (is_dir(path))
                printf("%s: Directory\n", argv[i]);
            else if (is_file(path))
                printf("%s: File\n", argv[i]);
            else
                printf("%s: File not found!\n", argv[i]);
        }
    }
    exit();
}
