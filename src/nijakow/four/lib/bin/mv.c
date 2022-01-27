inherits "/std/app.c";

void start()
{
    if (length(argv) != 3)
        connection()->write("Argument error!\n");
    else {
        string from = resolve(pwd(), argv[1]);
        string to   = resolve(pwd(), argv[2]);
        if (from == nil || to == nil || !mv(from, to)) {
            connection()->write("File not found!\n");
        }
    }
    exit();
}
