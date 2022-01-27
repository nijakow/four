inherits "/std/cmd.c";

void start()
{
    connection()->write("Starting the shell...\n");
    execappfromcli(this->exit, "/bin/sh.c", "/", {"/bin/sh.c"});
}
