inherits "/std/app.c";

void start()
{
    if (length(argv) <= 1)
        connection()->write("Argument error!\n");
    else {
        for (int i = 1; i < length(argv); i++) {
           if (!rm(resolve(pwd(), argv[i])))
                connection()->write(argv[i], ": error.\n");
        }
    }
    exit();
}
