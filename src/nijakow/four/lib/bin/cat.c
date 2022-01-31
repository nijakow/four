inherits "/std/app.c";

void start()
{
    for (int i = 1; i < length(argv); i++)
    {
        string path = resolve(pwd(), argv[i]);
        string text = cat(path);
        if (text == nil)
            printf("%s: File not found!\n", argv[i]);
        else
            printf("%s", text);
    }
    exit();
}
