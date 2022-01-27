inherits "/std/cmd.c";

void start()
{
    connection()->write("Starting the shell...\n");
    execappfromcli(this->exit, "/usr/bin/shell.c", "/", {"/usr/bin/shell.c"});
}
