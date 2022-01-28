inherits "/std/cmd.c";

void start()
{
    execappfromcli(this->exit, "/bin/sh.c");
}
