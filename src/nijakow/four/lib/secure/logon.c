inherits "/lib/app.c";

void main()
{
    printf("Welcome to the MUD!\n");
    exec(exit, "/bin/sh.c", []);
}
