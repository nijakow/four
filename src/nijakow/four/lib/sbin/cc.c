inherits "/std/app.c";

void start()
{
    if (length(argv) <= 1)
        arg_error();
    else {
        for (int i = 1; i < length(argv); i++)
        {
            string file = resolve(pwd(), argv[i]);
            if (recompile(file)) {
                the(file);  // This automatically reinitializes the file
                connection()->mode_green();
                connection()->write(file, ": ok.\n");
                connection()->mode_normal();
            } else {
                connection()->mode_red();
                connection()->write(file, ": error.\n");
                connection()->mode_normal();
            }
        }
    }
    exit();
}
