inherits "/std/app.c";

void start()
{
    if (length(argv) == 1) {
        printf("Fournix\n");
    } else if (length(argv) == 2 && argv[1] == "-a") {
        printf("Fournix 0.1\n");
    } else {
        printf("Argument error!\n");
    }
    exit();
}
