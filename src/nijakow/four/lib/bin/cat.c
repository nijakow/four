inherits "/std/app.c";

void start()
{
    for (int i = 1; i < length(argv); i++)
    {
        string path = resolve(pwd(), argv[i]);
        string text = cat(path);
        if (text == nil)
            connection()->write(argv[i], ": File not found!\n");
        else
            connection()->write(text);
    }
    exit();
}
