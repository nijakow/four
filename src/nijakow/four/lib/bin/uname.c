inherits "/std/app.c";

void start()
{
    if (length(argv) == 1) {
        connection()->write("Fournix\n");
    } else if (length(argv) == 2 && argv[1] == "-a") {
        connection()->write("Fournix 0.1\n");
    } else {
        connection()->write("Argument error!\n");
    }
    exit();
}
