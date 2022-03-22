inherits "/std/app.c";

inherits "/std/lib/sys/open.c";
inherits "/std/lib/sys/read.c";
inherits "/std/lib/sys/close.c";

private void do_cat(string file)
{
    int    fd;
    int    bytes_read;
    char*  buffer;

    buffer = malloc(128);
    fd = open(file, 1);
    if (fd < 0) {
        printf("%s: file not found\n", file);
    } else {
        while (true) {
            bytes_read = read(fd, buffer);
            if (bytes_read <= 0)
                break;
            for (int x = 0; x < bytes_read; x++)
                printf("%c", buffer[x]);
        }
        close(fd);
    }
    exit();
}

void start()
{
    for (int i = 1; i < length(argv); i++)
    {
        do_cat(resolve(pwd(), argv[i]));
    }
    exit();
}
