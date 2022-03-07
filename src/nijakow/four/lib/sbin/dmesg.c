inherits "/std/app.c";

void start()
{
    int amount = -1;
    if (length(argv) == 2)
        amount = atoi(argv[1]);
    if (length(argv) < 1 || length(argv) > 2)
        printf("Argument error!\n");
    else {
        foreach (string s : getmsgs(amount))
            printf("%s\n", s);
    }
    exit();
}
