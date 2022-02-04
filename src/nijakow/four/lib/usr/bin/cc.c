inherits "/std/app.c";

void start()
{
    if (length(argv) <= 1)
        printf("Argument error!\n");
    else {
        for (int i = 1; i < length(argv); i++)
        {
            string file = resolve(pwd(), argv[i]);
            if (recompile(file)) {
                the(file);  // This automatically reinitializes the file
                connection()->mode_green();
                printf("%s: ok.\n", file);
                connection()->mode_normal();
            } else {
                connection()->mode_red();
                printf("%s: error.\n", file);
                connection()->mode_normal();
            }
        }
    }
    exit();
}
