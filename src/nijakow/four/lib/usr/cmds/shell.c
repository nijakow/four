inherits "/std/cmd.c";

void start()
{
    connection()->write("Starting the shell...\n");
    exec(this::exit, "/std/apps/shell.c");
}
