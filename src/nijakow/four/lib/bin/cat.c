inherits "/std/cli.c";

void start(list argv)
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
